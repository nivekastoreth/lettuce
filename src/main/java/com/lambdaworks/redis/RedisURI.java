package com.lambdaworks.redis;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:mark.paluch@1und1.de">Mark Paluch</a>
 * @since 14.05.14 21:30
 */
public class RedisURI implements Serializable {
    private String host;
    private String sentinelMasterId;
    private int port;
    private int database;
    private String password;
    private long timeout = 60;
    private TimeUnit unit = TimeUnit.SECONDS;

    public RedisURI() {
    }

    public RedisURI(String host, String sentinelMasterId, int port, int database, String password, long timeout, TimeUnit unit) {
        this.host = host;
        this.sentinelMasterId = sentinelMasterId;
        this.port = port;
        this.database = database;
        this.password = password;
        this.timeout = timeout;
        this.unit = unit;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getSentinelMasterId() {
        return sentinelMasterId;
    }

    public void setSentinelMasterId(String sentinelMasterId) {
        this.sentinelMasterId = sentinelMasterId;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public static class Builder {

        private RedisURI redisURI = new RedisURI();

        public static Builder redis(String host) {
            return redis(host, 6379);
        }

        public static Builder redis(String host, int port) {
            checkNotNull(host, "host must not be null");
            Builder builder = new Builder();
            builder.redisURI.setHost(host);
            builder.redisURI.setPort(port);
            return builder;
        }

        public static Builder sentinel(String host, String masterId) {
            return sentinel(host, masterId, 26379);
        }

        public static Builder sentinel(String host, String masterId, int port) {
            checkNotNull(host, "host must not be null");
            checkNotNull(masterId, "sentinelMasterId must not be null");
            Builder builder = new Builder();
            builder.redisURI.setSentinelMasterId(host);
            builder.redisURI.setPort(port);
            return builder;
        }

        public Builder withPort(int port) {
            redisURI.setPort(port);
            return this;
        }

        public Builder withDatabase(int database) {
            redisURI.setDatabase(database);
            return this;
        }

        public Builder withPassword(String password) {
            checkNotNull(password, "password must not be null");
            redisURI.setPassword(password);
            return this;
        }

        public Builder withTimeout(long timeout, TimeUnit unit) {
            checkNotNull(unit, "unit must not be null");
            redisURI.setTimeout(timeout);
            redisURI.setUnit(unit);
            return this;
        }

        public RedisURI build() {
            return redisURI;
        }

    }

}