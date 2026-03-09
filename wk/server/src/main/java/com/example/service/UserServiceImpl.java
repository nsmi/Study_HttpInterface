package com.example.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.model.FindUserCond;
import com.example.model.User;
import com.example.model.User2;

/**
 * {@link UserService} の実装クラス。
 *
 * <p>旧プロジェクト（HTTP Invoker）との違い:</p>
 * <ul>
 *   <li>旧: {@code application-context.xml} に以下を定義する必要があった:
 *       <ol>
 *         <li>{@code UserServiceImpl} を Bean として登録</li>
 *         <li>{@code HttpInvokerServiceExporter} でサービスを公開（Bean name="/userService"）</li>
 *       </ol></li>
 *   <li>新: {@code @RestController} を付与するだけ。XML 設定ファイル不要。
 *       {@code @SpringBootApplication} のコンポーネントスキャンで自動検出される。
 *       URL マッピングは {@link UserService} の {@code @HttpExchange} アノテーションから自動設定される</li>
 * </ul>
 *
 * <p>URL マッピングの仕組み（Spring 6.1 以降）:</p>
 * <pre>
 * {@code @HttpExchange(url = "/users")} → ベースパス /users
 * {@code @GetExchange}                  → GET  /users       → findAll()
 * {@code @GetExchange("/{id}")}         → GET  /users/{id}  → findById(id)
 *
 * フルURL例（コンテキストパスが /server の場合）:
 *   GET http://localhost:8080/server/users
 *   GET http://localhost:8080/server/users/1
 * </pre>
 *
 * <p>Spring MVC の処理フロー:</p>
 * <pre>
 * HTTP リクエスト
 *   → DispatcherServlet（Spring Boot が自動設定）
 *   → RequestMappingHandlerMapping（@HttpExchange を認識・マッピング）
 *   → UserServiceImpl のメソッド実行
 *   → 戻り値を Jackson が JSON にシリアライズ
 *   → HTTP レスポンス
 * </pre>
 */
@RestController
public class UserServiceImpl implements UserService {

    /**
     * チュートリアル用のインメモリデータ。
     * 実際のアプリケーションでは DB や Repository から取得する。
     */
    private final List<User> users = List.of(
            new User(1, "Alice", "alice@example.com"),
            new User(2, "Bob",   "bob@example.com"),
            new User(3, "Charlie", "charlie@example.com")
    );

    /**
     * 全ユーザーを返す。
     *
     * <p>Spring MVC が JSON にシリアライズして HTTP レスポンスとして返す。
     * {@code @RestController} により {@code @ResponseBody} が自動適用される。</p>
     */
    @Override
    public List<User> findAll() {
        return users;
    }

    /**
     * 指定 ID のユーザーを返す。存在しない場合は 404 を返す。
     *
     * <p>{@link ResponseStatusException} をスローすると、Spring MVC が
     * 対応する HTTP ステータスコードのレスポンスを生成する。</p>
     *
     * @param id ユーザー ID
     * @return 指定 ID のユーザー
     */
    @Override
    public User findById(int id) {
        return users.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found: id=" + id));
    }

	@Override
	public int findUser(FindUserCond cond) {
		// TODO 自動生成されたメソッド・スタブ
		List<User> user = cond.getUser();
		List<User2> user2 = cond.getUser2();
		
		User u1 = user.stream()
		        .filter(u -> u.getId() == 2)
		        .findFirst()
		        .orElse(null);
				
		int u1Int =  u1.getId();
		
		User2 u2 = user2.stream()
		        .filter(u -> u.getId() == 2)
		        .findFirst()
		        .orElse(null);
				
		int u2Int =  u2.getId();
		return u2Int + u1Int;
	}

	@Override
	public int err() {
		String str = null;          // null を代入
        System.out.println(str.length());
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}
}
