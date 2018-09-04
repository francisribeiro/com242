package App;

public class Group{

    private final String Name;
    private final String Password;

    public Group(String Name, String Password) {
        this.Name = Name;
        this.Password = Password;
    }

    public String getName() {
        return Name;
    }

    public String getPassword() {
        return Password;
    }
}
