package com.example.service;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.example.model.FindUserCond;
import com.example.model.User;

/**
 * ユーザーサービスの契約インターフェース。
 *
 * <p>旧プロジェクト（HTTP Invoker）との最大の違い:</p>
 * <ul>
 *   <li>旧: 普通の Java インターフェース。URL 情報は持たない。
 *       サーバー側は application-context.xml の {@code <bean name="/userService" ...>} で URL を定義。
 *       クライアント側は cfg.xml の serviceUrl プロパティで URL を定義。</li>
 *   <li>新: {@code @HttpExchange} アノテーションにより URL 情報をインターフェース自体が持つ。
 *       サーバー側・クライアント側ともにこのインターフェースを参照するだけで
 *       URL マッピングが決まる。XML 設定ファイルは不要。</li>
 * </ul>
 *
 * <p>このインターフェースはサーバー・クライアントの共通契約として機能する:</p>
 * <ul>
 *   <li>サーバー側: {@link UserServiceImpl} がこのインターフェースを実装し {@code @RestController} を付与する。
 *       Spring MVC（Spring 6.1 以降）が {@code @HttpExchange} を認識して URL マッピングを設定する。</li>
 *   <li>クライアント側: {@code HttpServiceProxyFactory} がこのインターフェースの動的プロキシを生成する。
 *       メソッド呼び出し時にアノテーションを解釈して HTTP リクエストを組み立てる。</li>
 * </ul>
 *
 * <p>インターフェース共有の仕組み（旧プロジェクト踏襲）:</p>
 * <ul>
 *   <li>このインターフェースは server プロジェクトで定義する</li>
 *   <li>{@code mvn install} で {@code server-1.0-SNAPSHOT-classes.jar} が生成される</li>
 *   <li>クライアントの pom.xml が {@code classifier=classes} でこの JAR を参照する</li>
 * </ul>
 */
@HttpExchange(
        url = "/users",           // このサービスのベース URL パス
        accept = "application/json",      // クライアントが受け入れるレスポンス形式
        contentType = "application/json"  // クライアントが送信するリクエスト形式
)
public interface UserService {

    /**
     * 全ユーザーを取得する。
     *
     * <p>URL マッピング:</p>
     * <ul>
     *   <li>サーバー側: GET /users → {@code findAll()} にマッピング</li>
     *   <li>クライアント側: {@code userService.findAll()} → GET http://[host]/[context]/users を送信</li>
     * </ul>
     *
     * @return 全ユーザーのリスト
     */
    @GetExchange
    List<User> findAll();

    /**
     * 指定 ID のユーザーを取得する。
     *
     * <p>URL マッピング:</p>
     * <ul>
     *   <li>サーバー側: GET /users/{id} → {@code findById(id)} にマッピング</li>
     *   <li>クライアント側: {@code userService.findById(1L)} → GET http://[host]/[context]/users/1 を送信</li>
     * </ul>
     *
     * <p>{@code @PathVariable} は Spring MVC とクライアント側 HTTP Interface の両方で
     * パスパラメータのバインディングに使用する。</p>
     *
     * @param id ユーザー ID
     * @return 指定 ID のユーザー
     */
    @GetExchange("/{id}")
    User findById(@PathVariable int id);
    
    @GetExchange("/users/find")
    int findUser(@RequestBody FindUserCond cond);
    
    @GetExchange("/users/err")
    int err();
}
