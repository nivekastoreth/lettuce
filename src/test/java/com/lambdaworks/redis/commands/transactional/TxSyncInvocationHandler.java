package com.lambdaworks.redis.commands.transactional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import com.lambdaworks.redis.internal.AbstractInvocationHandler;

/**
 * Invocation handler for testing purposes that wraps each call into a transaction.
 * 
 * @param <K>
 * @param <V>
 */
public class TxSyncInvocationHandler<K, V> extends AbstractInvocationHandler {

    private final Object api;
    private final Method multi;
    private final Method discard;
    private final Method exec;
    private final Method ping;

    public TxSyncInvocationHandler(Object api) throws Exception {

        this.api = api;
        this.multi = api.getClass().getMethod("multi");
        this.exec = api.getClass().getMethod("exec");
        this.discard = api.getClass().getMethod("discard");
        this.ping = api.getClass().getMethod("ping");
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {

        try {

            if (method.getName().equals("exec") || method.getName().equals("multi")) {
                throw new IllegalStateException("Cannot execute transaction commands over this transactional wrapper");
            }

            Method targetMethod = api.getClass().getMethod(method.getName(), method.getParameterTypes());

            if (!method.getName().equals("close")) {

                multi.invoke(api);
                ping.invoke(api);

                targetMethod.invoke(api, args);

                Object result = exec.invoke(api);

                if (result == null || !(result instanceof List<?>)) {
                    return result;
                }
                List<?> list = (List<?>) result;

                if (list.size() > 1) {

                    result = list.get(1);
                    if (result instanceof Exception) {
                        throw (Exception) result;
                    }

                    return result;
                }

                return null;
            }

            return targetMethod.invoke(api, args);

        } catch (InvocationTargetException e) {
            try {
                discard.invoke(api);
            } catch (Exception e1) {
            }
            throw e.getTargetException();
        }
    }

    /**
     * Create a transactional wrapper proxy for {@link RedisCommands}.
     * 
     * @param connection the connection
     * @return the wrapper proxy.
     */
    @SuppressWarnings("unchecked")
    public static <K, V> RedisCommands<K, V> sync(StatefulRedisConnection<K, V> connection) {

        try {
            TxSyncInvocationHandler<K, V> handler = new TxSyncInvocationHandler<>(connection.sync());
            return (RedisCommands<K, V>) Proxy.newProxyInstance(handler.getClass().getClassLoader(),
                    new Class<?>[] { RedisCommands.class }, handler);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}