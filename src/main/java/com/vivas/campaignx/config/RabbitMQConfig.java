package com.vivas.campaignx.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;

@Configuration
@EnableRabbit
@EnableTransactionManagement
public class RabbitMQConfig {
    @Value("${rabbit.host}")
    private String host;
    @Value("${rabbit.port}")
    private Integer port;
    @Value("${rabbit.username}")
    private String username;
    @Value("${rabbit.password}")
    private String password;
    @Value("${queueNotify}")
    private String queueNameNotify;


    @Bean
    @Primary
    public ConnectionFactory connectionFactory() throws Exception {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() throws Exception {
        RabbitTemplate rt = new RabbitTemplate(connectionFactory());
        rt.setChannelTransacted(true);
        return rt;
    }

    @Bean
    public Queue queueNotify() {
        return new Queue(queueNameNotify);

    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerlistenerFactory() throws Exception {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        return factory;
    }

    @Bean (name = "rabbitTransactionManager")
    public RabbitTransactionManager rabbitTransactionManager(ConnectionFactory connectionFactory) {
        return new RabbitTransactionManager(connectionFactory);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @PostConstruct
    public void init() throws Exception {
        rabbitTemplate().setChannelTransacted(true);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
