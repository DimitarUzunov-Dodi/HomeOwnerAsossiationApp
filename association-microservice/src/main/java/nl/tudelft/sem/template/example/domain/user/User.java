package nl.tudelft.sem.template.example.domain.user;

import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "Users")
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, unique = true)
    private int id;

    /*
    the userId of the account
    also unique key
     */
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    /*
    personal name for display
     */
    @Column(name = "name", nullable = false)
    private String name;


    //TODO
    //realistic field such as contact email etc


    public User(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
