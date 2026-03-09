package com.example;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.web.client.ResourceAccessException;

import com.example.error.RemoteApiException;
import com.example.model.FindUserCond;
import com.example.model.User;
import com.example.model.User2;
import com.example.service.UserService;

/**
 * HTTP Interface クライアントのエントリポイント。
 *
 * <p>旧プロジェクト（HTTP Invoker）との最大の違い:</p>
 * <pre>
 * 【旧: HTTP Invoker + cfg.xml】
 *   cfg.xml（XML設定）で接続先URLとインターフェースを定義
 *     → HttpInvokerProxyFactoryBean がプロキシを生成
 *     → Java シリアライズ（バイナリ）で通信
 *
 * 【新: HTTP Interface + RestClient】
 *   コード内で RestClient と HttpServiceProxyFactory を構築
 *     → HttpServiceProxyFactory が @HttpExchange を解釈してプロキシを生成
 *     → JSON で通信（言語非依存・標準的な Web API）
 * </pre>
 *
 * <p>クライアントが動的プロキシを通じてサーバーを呼び出す流れ:</p>
 * <pre>
 * userService.findAll() 呼び出し
 *   → JDK 動的プロキシの InvocationHandler が処理
 *   → @HttpExchange(url="/users") + @GetExchange を解釈
 *   → RestClient で GET http://localhost:8080/server/users を送信
 *   → レスポンスの JSON を Jackson が List&lt;User&gt; にデシリアライズ
 *   → 呼び出し元に返す
 * </pre>
 */
public class ClientMain {
	
    private static final List<User> USER_LIST = Arrays.asList(
            new User(1, "田中 太郎", "taro.tanaka@example.com"),
            new User(2, "鈴木 花子", "hanako.suzuki@example.com"),
            new User(3, "佐藤 次郎", "jiro.sato@example.com")
    );
    
    private static final List<User2> USER_LIST2 = Arrays.asList(
            new User2(1, "田中 太郎", "taro.tanaka@example.com"),
            new User2(2, "鈴木 花子", "hanako.suzuki@example.com"),
            new User2(3, "佐藤 次郎", "jiro.sato@example.com")
    );

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println(" Spring HTTP Interface クライアント 起動");
        System.out.println("==============================================");

        // =====================================================
        // RestClient の構築
        // =====================================================
        // RestClient: Spring 6.1 で導入されたモダンな HTTP クライアント。
        // RestTemplate の後継。baseUrl を設定することで、以降のリクエストはこの URL を基点にする。
        //
        // コンテキストパス /server は Eclipse WTP の context-root 設定に対応する。
        // （旧プロジェクトでは cfg.xml の serviceUrl プロパティに直接 URL を記述していた）
        String baseUrl = "http://localhost:8080/server";
//        RestClient restClient = RestClient.builder()
//                .baseUrl(baseUrl)
//                .build();

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
//        HttpServiceProxyFactory factory = HttpServiceProxyFactory
//                .builderFor(RestClientAdapter.create(restClient))
//                .build();

        // UserService の動的プロキシを取得する。
        // 実体は JDK 動的プロキシ。メソッド呼び出しを HTTP リクエストに変換する。
        // 旧プロジェクトでは cfg.xml の ClassPathXmlApplicationContext 経由で取得していた。
        //UserService userService = factory.createClient(UserService.class);
        
        //UserService userService = BeanCommon.createClientUtile(UserService.class);
        UserService userService = BeanCommon.orderCreateClientUtile(UserService.class);

        try {
            // =====================================================
            // findAll() の呼び出し
            // =====================================================
            // プロキシが @GetExchange → GET /users に変換してサーバーに送信する。
            // レスポンスの JSON "[{...},{...}]" を Jackson が List<User> にデシリアライズする。
            System.out.println("\n--- findAll() 呼び出し ---");
            System.out.println("送信: GET " + baseUrl + "/users");
            List<User> users = userService.findAll();
            System.out.println("受信 (" + users.size() + " 件):");
            users.forEach(u -> System.out.println("  " + u));
            
            // --------------------------------------------------
            // テストtmp: ID指定での1件取得（存在するID）
            // --------------------------------------------------
            System.out.println("\n--- findUser 呼び出し ---");
            FindUserCond cond = new FindUserCond();
            cond.setUser(USER_LIST);
            cond.setUser2(USER_LIST2);
            int countUser = userService.findUser(cond);
            System.out.println(countUser);

            // =====================================================
            // findById() の呼び出し
            // =====================================================
            // プロキシが @GetExchange("/{id}") の {id} に 1L を代入し、
            // GET /users/1 に変換してサーバーに送信する。
            System.out.println("\n--- findById(1L) 呼び出し ---");
            System.out.println("送信: GET " + baseUrl + "/users/1");
            User user = userService.findById(1);
            System.out.println("受信: " + user);

            // =====================================================
            // 存在しない ID の呼び出し（404 確認）
            // =====================================================
            System.out.println("\n--- findById(999L) 呼び出し（存在しない ID）---");
            System.out.println("送信: GET " + baseUrl + "/users/999");
            User notFound = userService.findById(999);
            //System.out.println("受信: " + notFound);
            
            // =====================================================
            // エラー処理の呼び出し（エラー 確認）
            // =====================================================
            System.out.println("\n--- エラー処理呼び出し---");
            System.out.println("送信: GET " + baseUrl + "/users/err");
            userService.err();
            System.out.println("受信: ");

        } catch (RemoteApiException ex) {
            // サーバが ProblemDetail を返した場合は、この1例外に集約される。
            System.err.println("Remote API error");
            System.err.println("  status=" + ex.getStatusCode().value());
            System.err.println("  code=" + ex.getErrorCode());
            System.err.println("  retryable=" + ex.isRetryable());
            System.err.println("  title=" + ex.getTitle());
            System.err.println("  detail=" + ex.getDetail());
            System.err.println("  path=" + Objects.toString(ex.getPath(), "N/A"));
        } catch (ResourceAccessException ex) {
            // DNS 失敗、接続拒否、読み取りタイムアウトなどは別系統で捕捉する。
            System.err.println("Remote API is unreachable: " + ex.getMessage());
        } catch (Exception e) {
            System.err.println("エラーが発生しました: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n==============================================");
        System.out.println(" 処理完了");
        System.out.println("==============================================");
    }
}
