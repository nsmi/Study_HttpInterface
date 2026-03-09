package com.example;

import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

public class BeanCommon {

	public <T> T doSomething(T input) {
	    // T は呼び出し時に決まる型
	    return input;
	}
	
	public static <T> T createClientUtile(Class<T> inputClass) {
	    // T は呼び出し時に決まる型
		
        // =====================================================
        // RestClient の構築
        // =====================================================
        // RestClient: Spring 6.1 で導入されたモダンな HTTP クライアント。
        // RestTemplate の後継。baseUrl を設定することで、以降のリクエストはこの URL を基点にする。
        //
        // コンテキストパス /server は Eclipse WTP の context-root 設定に対応する。
        // （旧プロジェクトでは cfg.xml の serviceUrl プロパティに直接 URL を記述していた）
        String baseUrl = "http://localhost:8080/server";
        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();

        // =====================================================
        // HTTP Interface プロキシの生成
        // =====================================================
        // RestClientAdapter: RestClient を HttpServiceProxyFactory に渡すためのアダプター。
        //   HttpServiceProxyFactory はさまざまな HTTP クライアント（RestClient, WebClient 等）に
        //   対応するため、アダプターパターンを採用している。
        //
        // HttpServiceProxyFactory: @HttpExchange アノテーションを解釈して
        //   動的プロキシを生成するファクトリー。
        //   旧プロジェクトの HttpInvokerProxyFactoryBean に相当する役割を担う。
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();
        
	    return factory.createClient(inputClass);
	}
}
