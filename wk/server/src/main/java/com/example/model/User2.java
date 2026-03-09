package com.example.model;

import java.io.Serializable;

/**
 * ユーザー情報を表すDTOクラス。
 *
 * DTO (Data Transfer Object) とは:
 *   クライアントとサーバー間でデータをやり取りするためのオブジェクト。
 *   ビジネスロジックは持たず、データの保持のみを担う。
 *
 *【重要】Serializable を実装する理由:
 *   Spring HTTP Invoker はオブジェクトを Java の標準シリアライズ機能でバイト列に変換し、
 *   HTTP のリクエスト/レスポンスボディとして送受信する。
 *   そのため、通信に使うすべてのクラスが Serializable を実装している必要がある。
 *   未実装の場合、通信時に NotSerializableException が発生する。
 *
 * 【パターン1の注意点】:
 *   このクラスはサーバー側の pom.xml で classes.jar として生成され、
 *   クライアント側の pom.xml から classifier=classes で参照される。
 *   そのためクライアント側にも同じバイトコードが届き、
 *   シリアライズ/デシリアライズでクラスの整合性が取れる。
 */
public class User2 implements Serializable {

    /**
     * シリアライズのバージョン識別子。
     *
     * なぜ定義するか:
     *   Serializable を実装するクラスを変更した際に、
     *   古いバイト列と新しいクラス定義の互換性チェックに使われる。
     *   明示的に定義しないと JVM が自動生成するが、
     *   クラス変更のたびに値が変わり、デシリアライズ失敗の原因になるため
     *   明示的に固定値を定義することが推奨されている。
     */
    private static final long serialVersionUID = 1L;

    /** ユーザーの一意識別ID */
    private int id;

    /** ユーザー名 */
    private String name;

    /** メールアドレス */
    private String email;

    /**
     * デフォルトコンストラクタ。
     * Java のシリアライズ/デシリアライズではデフォルトコンストラクタが必要になる場合があるため定義する。
     */
    public User2() {
    }

    /**
     * 全フィールドを初期化するコンストラクタ。
     *
     * @param id    ユーザーID
     * @param name  ユーザー名
     * @param email メールアドレス
     */
    public User2(int id, String name, String email) {
        this.id    = id;
        this.name  = name;
        this.email = email;
    }

    // =============================================
    // Getter / Setter
    // =============================================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * デバッグ用の文字列表現。
     * クライアント側で受信結果を表示する際に使用する。
     */
    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "'}";
    }
}
