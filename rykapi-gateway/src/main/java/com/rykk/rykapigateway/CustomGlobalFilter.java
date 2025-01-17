package com.rykk.rykapigateway;

import com.rykk.rykapiclientsdk.utils.SignUtils;
import com.rykk.rykapicommon.model.entity.InterfaceInfo;
import com.rykk.rykapicommon.model.entity.User;
import com.rykk.rykapicommon.service.InnerInterfaceInfoService;
import com.rykk.rykapicommon.service.InnerUserInterfaceInfoService;
import com.rykk.rykapicommon.service.InnerUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 全局请求过滤
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");
    private static final Long FIVE_MINUTES = 60 * 5L;//五分钟常量

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    // 这个常量应该从数据库中读取，因为每个接口的Host不一定相同
    private static final String INTERFACE_HOST = "http://localhost:5411";

    /**
     * @param exchange 路由交换机
     * @param chain    责任链模式，就是链式过滤
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 用户请求发送到API网关
        ServerHttpRequest request = exchange.getRequest();
        String path = INTERFACE_HOST + request.getPath().value();
        String method = request.getMethod().toString();
        // 2. 请求日志记录
        log.info("请求唯一标识：" + request.getId());
        log.info("请求路径：" + path);
        log.info("请求方法：" + method);
        log.info("请求参数：" + request.getQueryParams());
        String sourceAddress = Objects.requireNonNull(request.getRemoteAddress()).getHostString();
//        InetAddress address = request.getLocalAddress().getAddress();
//        String hostName = request.getLocalAddress().getHostName();
//        int port = request.getLocalAddress().getPort();
        log.info("请求来源地址IP：" + sourceAddress);
        log.info("请求来源地址：" + request.getRemoteAddress());
        // 拿到响应对象
        ServerHttpResponse response = exchange.getResponse();
        // 3. 黑白名单记录
        if (!IP_WHITE_LIST.contains(sourceAddress)) {
            return handleNoAuth(response);
        }
        // 4. 用户鉴权（AK\SK）
        HttpHeaders headers = request.getHeaders();
        //从请求头中获取ak、sk(sk在签名中隐藏)
        String accessKey = headers.getFirst("accessKey");
//        String secretKey = request.getHeader("secretKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");
        User invokeUser = null;
        // 通过AK查询数据库，检验用户是否合法
        try {
            invokeUser = innerUserService.getInvokeUser(accessKey);
        } catch (Exception e) {
            log.error("getInvokeUser error", e);
        }
        if (invokeUser == null) {
            return handleNoAuth(response);
        }
        //检验随机数范围，当然也可以周期性使用固定的单次随机数
        if (Long.parseLong(nonce) > 10000) {
            return handleNoAuth(response);
        }
        // 检验时间是否在5分钟内
        long currentTime = System.currentTimeMillis() / 1000;
        if ((currentTime - Long.parseLong((timestamp)) > FIVE_MINUTES)) {
            return handleNoAuth(response);
        }
        // 校验签名
        String secretKey = invokeUser.getSecretKey();
        String severSign = SignUtils.genSign(body, secretKey);
        if (!severSign.equals(sign)) {
            return handleNoAuth(response);
        }
        // 5. 判断请求的接口是否存在(在数据库中
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);
        } catch (Exception e) {
            log.error("getInterfaceInfo error", e);
        }
        if (interfaceInfo == null) {
            return handleNoAuth(response);
        }
        // 6. 请求转发，调用实际的接口
        Mono<Void> filter = chain.filter(exchange);
        // 7. 响应日志
        log.info("响应：" + response.getStatusCode());
        // 8. 调用成功，接口调用次数+1
        Long interfaceInfoId = interfaceInfo.getId();
        Long userId = invokeUser.getId();
        return handleResponse(exchange, chain, interfaceInfoId, userId);
/*        if (response.getStatusCode() == HttpStatus.OK) {
            //调用次数+1
        } else {
            return handleInvokeError(response);
        }
        // 9. 调用失败，返回一个规范的错误码
        log.info("custom global filter");
        return chain.filter(exchange);//放行*/
    }

    @Override
    public int getOrder() {
        return -1;
    }

    public Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    public Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }


    /**
     * 处理响应
     *
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, Long interfaceInfoId, Long userId) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            // 往返回值里写数据
                            // 拼接字符串
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> {
                                        // 7. 调用成功，接口调用次数 + 1 invokeCount
                                        try {
                                            innerUserInterfaceInfoService.invokeCount(interfaceInfoId, userId);
                                        } catch (Exception e) {
                                            log.error("invokeCount error", e);
                                        }
                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        DataBufferUtils.release(dataBuffer);//释放掉内存
                                        // 构建日志
                                        StringBuilder sb2 = new StringBuilder(200);
                                        List<Object> rspArgs = new ArrayList<>();
                                        rspArgs.add(originalResponse.getStatusCode());
                                        String data = new String(content, StandardCharsets.UTF_8); //data
                                        sb2.append(data);
                                        // 打印日志
                                        log.info("响应结果：" + data);
                                        return bufferFactory.wrap(content);
                                    }));
                        } else {
                            // 8. 调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange); // 降级处理返回数据
        } catch (Exception e) {
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }
}

