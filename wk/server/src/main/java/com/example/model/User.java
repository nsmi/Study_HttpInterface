package com.example.model;

/**
 * ユーザー情報を表す DTO（Data Transfer Object）クラス。
 *
 * <p>旧プロジェクト（HTTP Invoker）との違い:</p>
 * <ul>
 *   <li>旧: Java シリアライズ（バイナリ通信）のため {@code implements Serializable} が必要だった</li>
 *   <li>新: JSON 通信のため Serializable は不要。
 *       Jackson がフィールドを JSON へ変換するため、
 *       デフォルトコンストラクタとゲッター/セッターを持つ普通の POJO で動作する</li>
 * </ul>
 *
 * <p>このクラスは server プロジェクトで定義されるが、
 * maven-war-plugin の attachClasses=true により生成される
 * {@code server-1.0-SNAPSHOT-classes.jar} に含まれ、クライアントと共有される。</p>
 */
public class User {

    /** ユーザー ID */
    private int id;

    /** ユーザー名 */
    private String name;

    /** メールアドレス */
    private String email;

    /**
     * Jackson がレスポンスの JSON をデシリアライズする際に使用するデフォルトコンストラクタ。
     * Jackson はデフォルトコンストラクタでインスタンスを生成し、セッターで値をセットする。
     */
    public User() {}

    /**
     * サーバー内部でのインスタンス生成用コンストラクタ。
     *
     * @param id    ユーザー ID
     * @param name  ユーザー名
     * @param email メールアドレス
     */
    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "'}";
    }
}
