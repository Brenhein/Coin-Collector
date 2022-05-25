package edu.msu.defenso2.project3.Cloud;

import android.util.Log;

import java.io.IOException;

import edu.msu.defenso2.project3.Cloud.Models.AddCoins;
import edu.msu.defenso2.project3.Cloud.Models.CreateResult;
import edu.msu.defenso2.project3.Cloud.Models.LoadDataResult;
import edu.msu.defenso2.project3.Cloud.Models.LoginResult;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * This class handles the network operations
 */
public class Cloud {
    /**
     * Server paths
     */
    private static final String BASE_URL = "https://webdev.cse.msu.edu/~veduamat/cse476/project3/";
    public static final String LOGIN_PATH = "login.php";
    public static final String CREATE_PATH = "registration.php";
    public static final String LOAD_DATA_PATH = "list-users.php";
    public static final String ADD_DATA_PATH = "retrieve.php";
    public static final String NEAR_COIN_PATH = "nearcoin.php";


    /**
     * Builds the retrofit
     */
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build();


    /**
     * Logs the user into their account for the game
     * @param username The username of the user
     * @param password The password of the user
     * @return If it was successful or not
     */
    public boolean loginFromCloud(String username, String password) {
        GameService service = retrofit.create(GameService.class);
        try {
            Response<LoginResult> response = service.login(username, password).execute();

            // check if request failed
            if (!response.isSuccessful()) {
                Log.e("LoginFromCloud", "Failed to login, response code is = " + response.code());
                return false;
            }

            LoginResult result = response.body();
            if (result.getStatus().equals("success")) {
                return true;
            }

            Log.e("LoginFromCloud", "Failed to login, message is = '" + result.getMessage() + "'");
            return false;
        } catch (IOException e) {
            Log.e("LoginFromCloud", "Exception occurred while logging on!", e);
            return false;
        }
    }


    /**
     * Open a connection to a hatting in the cloud.
     * @param username id for the hatting
     * @return boolean indicating success
     */
    public boolean createFromCloud(String username, String password) {

        GameService service = retrofit.create(GameService.class);
        try {
            Response<CreateResult> response = service.createAccount(username, password).execute();

            // check if request failed
            if (!response.isSuccessful()) {
                Log.e("CreateFromCloud", "Failed to create account, response code is = " + response.code());
                return false;
            }

            CreateResult result = response.body();
            if (result.getStatus().equals("success")) {
                return true;
            }

            Log.e("CreateFromCloud", "Failed to create account, message is = '" + result.getMessage() + "'");
            return false;
        } catch (IOException e) {
            Log.e("CreateFromCloud", "Exception occurred while creating account!", e);
            return false;
        }
    }


    /**
     * Loads the coins for a current user from the cloud
     * @param username the username of the user
     * @return if it was successful or not
     */
    public int loadFromCloud(String username) {
        GameService service = retrofit.create(GameService.class);
        try {
            Response<LoadDataResult> response = service.loadData(username).execute();

            // check if request failed
            if (!response.isSuccessful()) {
                Log.e("LoadFromCloud", "Failed to load user data, response code is = " + response.code());
                return -1;
            }

            LoadDataResult result = response.body();
            if (result.getStatus().equals("success")) {
                return result.getCoins();
            }

            Log.e("CreateFromCloud", "Failed to load user data, message is = '" + result.getMessage() + "'");
            return -1;
        } catch (IOException e) {
            Log.e("CreateFromCloud", "Exception occurred while loading user data!", e);
            return -1;
        }
    }


    /**
     * Adds coins to user
     * @param username
     * @param coins
     * @return
     */
    public boolean addCoins(String username, int coins) {

        GameService service = retrofit.create(GameService.class);
        try {
            Response<AddCoins> response = service.addCoins(username, coins).execute();

            // check if request failed
            if (!response.isSuccessful()) {
                Log.e("AddToCloud", "Failed to add coins, response code is = " + response.code());
                return false;
            }

            AddCoins result = response.body();
            if (result.getStatus().equals("success")) {
                return true;
            }

            Log.e("AddToCloud", "Failed to add coins, message is = '" + result.getMessage() + "'");
            return false;
        } catch (IOException e) {
            Log.e("AddToCloud", "Exception occurred while adding coins!", e);
            return false;
        }
    }


    /**
     * Determines if the user is near a coin
     *
     * @param position LatLng object with the user position
     * @return true if close to a coin, false otherwise
     */
    /*public boolean isNearCoin(LatLng position) {
        GameService service = retrofit.create(GameService.class);
        if (position != null) {
            try {
                Response<IsNearResult> response = service.isNearCoin(position.latitude, position.longitude).execute();

                // check if request failed
                if (!response.isSuccessful()) {
                    Log.e("AddToCloud", "Failed to check if near coin, response code is = " + response.code());
                    return false;
                }

                IsNearResult result = response.body();
                if (result.getStatus().equals("success")) {
                    return true;
                }

                Log.e("AddToCloud", "Failed to check if near coin");
                return false;
            } catch (IOException e) {
                Log.e("AddToCloud", "Exception occurred while checking if near coin!", e);
                return false;
            }
        } else {
            return false;
        }
    }*/
}
