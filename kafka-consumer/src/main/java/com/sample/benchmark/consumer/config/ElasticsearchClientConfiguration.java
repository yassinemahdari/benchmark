package com.sample.benchmark.consumer.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.TransportUtils;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchClient;

import javax.net.ssl.SSLContext;
import java.io.IOException;

@Configuration
public class ElasticsearchClientConfiguration {

    @Value("${http_ca_path:classpath:http_ca.crt}")
    private Resource httpCAPath;

    @Bean
    public ElasticsearchClient elasticsearchClient(ElasticsearchProperties elasticsearchProperties) throws IOException {
        SSLContext sslContext = TransportUtils.sslContextFromHttpCaCrt(httpCAPath.getFile());

        BasicCredentialsProvider credsProv = new BasicCredentialsProvider();
        credsProv.setCredentials(
                AuthScope.ANY, new UsernamePasswordCredentials(elasticsearchProperties.getUsername(), elasticsearchProperties.getPassword())
        );

        RestClient restClient = RestClient
                .builder(new HttpHost("localhost", 9200, "https"))
                .setHttpClientConfigCallback(hc -> hc
                        .setSSLContext(sslContext)
                        .setDefaultCredentialsProvider(credsProv)
                )
                .build();

        // Create the transport and the API client
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }

    @Bean
    public ReactiveElasticsearchClient reactiveElasticsearchClient(ElasticsearchProperties elasticsearchProperties) throws IOException {
        SSLContext sslContext = TransportUtils.sslContextFromHttpCaCrt(httpCAPath.getFile());

        BasicCredentialsProvider credsProv = new BasicCredentialsProvider();
        credsProv.setCredentials(
                AuthScope.ANY, new UsernamePasswordCredentials(elasticsearchProperties.getUsername(), elasticsearchProperties.getPassword())
        );

        RestClient restClient = RestClient
                .builder(new HttpHost("localhost", 9200, "https"))
                .setHttpClientConfigCallback(hc -> hc
                        .setSSLContext(sslContext)
                        .setDefaultCredentialsProvider(credsProv)
                )
                .build();

        // Create the transport and the API client
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new ReactiveElasticsearchClient(transport);
    }

}
