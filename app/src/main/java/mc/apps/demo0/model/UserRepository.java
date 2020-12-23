package mc.apps.demo0.model;


public class UserRepository {
    /**
     * Données de Tests pour présentation!
     */
    User[] users = {
            new User(1,"Ryan","Chouarbi","ryan@gmail.com","admin",Profil.Administrateur),
            new User(2,"Maelys","Chouarbi","maelys@gmail.com","superv",Profil.Superviseur),
            new User(3,"John","Doe","tech@gmail.com","tech",Profil.Technicien)
    };
    public User find(String login, String password){
        for (User user: users) {
            if(user.getEmail().equals(login) && user.getPassword().equals(password)){
                return user;
            }
        }
        return null;
    }
}
