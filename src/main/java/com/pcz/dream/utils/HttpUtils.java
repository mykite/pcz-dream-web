package com.pcz.dream.utils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultHttpResponseParserFactory;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.util.EntityUtils;

public class HttpUtils {

	private static PoolingHttpClientConnectionManager manager = null;

	private static CloseableHttpClient httpClient = null;

	public static synchronized CloseableHttpClient getHttpClient() {
		if (httpClient == null) {
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
					.register("http", PlainConnectionSocketFactory.INSTANCE)
					.register("https", SSLConnectionSocketFactory.getSystemSocketFactory()).build();

			HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory = new ManagedHttpClientConnectionFactory(
					DefaultHttpRequestWriterFactory.INSTANCE, DefaultHttpResponseParserFactory.INSTANCE);
			// dns解析器
			DnsResolver dnsResolver = SystemDefaultDnsResolver.INSTANCE;
			// 创建连接池管理器
			manager = new PoolingHttpClientConnectionManager(socketFactoryRegistry, connFactory, dnsResolver);
			// 默认为socket配置
			SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
			manager.setDefaultSocketConfig(socketConfig);
			// 设置整个连接池的醉大连接数
			manager.setMaxTotal(300);
			// 每个路由的默认最大连接，每个路由实际最大连接数默认为DefaultMaxPreRotue控制而MaxTotal是控制整个池最大数
			manager.setDefaultMaxPerRoute(200);
			manager.setValidateAfterInactivity(5000);// 在从连接词获取连接时，连接不活跃多长时间后需要进行一次验证默认为2S
			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(2000).// 连接超时时间
					setSocketTimeout(5000).// 等待数据超时时间
					setConnectionRequestTimeout(2000).build();// 设置从连接池获取连接的等待超时时间
			httpClient = HttpClients.custom().setConnectionManager(manager).setConnectionManagerShared(false)// 连接池不是共享模式
					.evictIdleConnections(40, TimeUnit.SECONDS)// 定期空闲连接
					.evictExpiredConnections().// 定期回收过期连接
					setConnectionTimeToLive(60, TimeUnit.SECONDS).// 连接存活时间，如果不设置，则根据长连接信息决定
					setDefaultRequestConfig(requestConfig).// 默认请求配置
					setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE).// 连接重用策略，既是否能keepAlive
					setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE).// 长连接配置，既获取长连接生产多少时间
					setRetryHandler(new DefaultHttpRequestRetryHandler(0, false)).// 设置重试次数，默认为3次，当前禁用需要时再开启
					build();

			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					try {
						httpClient.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			return httpClient;
		} else {
			return httpClient;
		}
	}
	
}
