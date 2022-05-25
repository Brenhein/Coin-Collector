package edu.msu.defenso2.project3.Cloud;

import edu.msu.defenso2.project3.Cloud.Models.AddCoins;
import edu.msu.defenso2.project3.Cloud.Models.CreateResult;
import edu.msu.defenso2.project3.Cloud.Models.IsNearResult;
import edu.msu.defenso2.project3.Cloud.Models.LoadDataResult;
import edu.msu.defenso2.project3.Cloud.Models.LoginResult;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import static edu.msu.defenso2.project3.Cloud.Cloud.ADD_DATA_PATH;
import static edu.msu.defenso2.project3.Cloud.Cloud.CREATE_PATH;
import static edu.msu.defenso2.project3.Cloud.Cloud.LOAD_DATA_PATH;
import static edu.msu.defenso2.project3.Cloud.Cloud.LOGIN_PATH;
import static edu.msu.defenso2.project3.Cloud.Cloud.NEAR_COIN_PATH;

public interface GameService {
    /**
     * Logs User into their account
     */
    @GET(LOGIN_PATH)
    Call<LoginResult> login(
            @Query("user") String userId,
            @Query("pw") String password
    );

    /**
     * Creates the user account, assuming validity in the username and password
     */
    @FormUrlEncoded
    @POST(CREATE_PATH)
    Call<CreateResult> createAccount(
            @Field("user") String userId,
            @Field("pw") String password
    );

    /**
     * Loads the data at the start of a game
     */
    @GET(LOAD_DATA_PATH)
    Call<LoadDataResult> loadData(
            @Query("user") String userId
    );


    /**
     * Adds to the user coin count
     */
    @GET(ADD_DATA_PATH)
    Call<AddCoins> addCoins(
            @Query("user") String userId,
            @Query("coin") int coin
    );

    /**
     * Loads the data at the start of a game
     */
    @GET(NEAR_COIN_PATH)
    Call<IsNearResult> isNearCoin(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude
    );
}
