package busdriver.com.vidriver;

/**
 * Created by richardalexander on 16/02/16.
 */
public class User {

    public String name, username, password, image, numero;
    int edad, tipoveh;

    public User(String name, int edad, int tipoveh, String username, String password, String image, String numero) {
        this.name = name;
        this.edad = edad;
        this.tipoveh = tipoveh;
        this.username = username;
        this.password = password;
        this.image = image;
        this.numero = numero;
    }

    public User(String username , String password) {
        this("", 0, 0, username, password, "", "");
    }
}