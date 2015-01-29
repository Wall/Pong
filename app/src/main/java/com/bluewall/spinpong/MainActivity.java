package com.bluewall.spinpong;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.bluewall.spinpong.UI.ScreenInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.android.gms.plus.Plus;

import java.util.ArrayList;
import java.util.List;

//import com.google.android.gms.games.multiplayer.realtime;

/**
 * Created by david on 12/13/14.
 */
public class MainActivity extends Activity implements RoomUpdateListener, RealTimeMessageReceivedListener, RoomStatusUpdateListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnInvitationReceivedListener {

    private static final int RC_SELECT_PLAYERS = 2;
    private GoogleApiClient mGoogleApiClient;
    private String mRoomId;
    // are we already playing?
    boolean mPlaying = false;

    // at least 2 players required for our game
    final static int MIN_PLAYERS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        //final int screenWidth = displayMetrics.widthPixels;
        //final int screenHeight = displayMetrics.heightPixels;

        ScreenInfo.RES_X = displayMetrics.widthPixels;
        ScreenInfo.RES_Y = displayMetrics.heightPixels;

        ActivityManager am  = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info  = am.getDeviceConfigurationInfo();
        //startQuickGame();
        System.out.println("gooogle:here1");
        /*mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .useDefaultAccount()
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();*/

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API, new Plus.PlusOptions.Builder()
                .addActivityTypes(
                        "http://schemas.google.com/AddActivity",
                        "http://schemas.google.com/BuyActivity")
                .build())
                .useDefaultAccount()
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

       /* mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
        */
        ((Button) findViewById(R.id.tutton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleApiClient.connect();
            }
        });
        //mGoogleApiClient.connect();

        System.out.println("gooogle:here2");
        // launch the player selection screen
        // minimum: 1 other player; maximum: 3 other players
        //Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, 1, 3);
        //startActivityForResult(intent, RC_SELECT_PLAYERS);
/*
        boolean supportsES2 = (info.reqGlEsVersion >= 0x20000);
        if (supportsES2) {
            MainRenderer mainRenderer = new MainRenderer(getApplicationContext());

            MainSurfaceView mainSurfaceView = new MainSurfaceView(this);
            mainSurfaceView.setEGLContextClientVersion(2);
            mainSurfaceView.setRenderer(mainRenderer);
            this.setContentView(mainSurfaceView);

            //mainRenderer.loadTexture(getApplicationContext(), R.drawable.basic_texture);

            final Shape shape = new Shape(new float[] {
                    -50, 0, 0,
                    0, 0, 0,
                    0, -50, 0},
                    new short[] {
                    0, 1, 2
                    }, new float[] {});
            final Pad pad = new Pad();
            final Ball ball = new Ball();
            ball.setPad(pad);
            //mainRenderer.add(shape2);
            mainRenderer.add(shape);
            mainRenderer.add(pad);
            mainRenderer.add(ball);
            mainRenderer.init();
            //pad.translate(0, 0.7f);
            mainSurfaceView.setOnGlobalTouchListener(new OnGlobalTouchListener() {
                @Override
                public boolean onTouch(MotionEvent event) {
                    //if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        //System.out.println("DOWN: " + event.getRawX() + ", " + event.getRawY());
                        pad.set(-event.getRawX() + ScreenInfo.RES_X/2, event.getRawY() - ScreenInfo.RES_Y/2);
                    //}
                    return true;
                }
            });

        } else {
            Log.e("OpenGLES 2", "Device does not support ES2.");
        }
*/
    }

    @Override
    public void onActivityResult(int request, int response, Intent data) {
        if (request == RC_SELECT_PLAYERS) {
            if (response != Activity.RESULT_OK) {
                // user canceled
                return;
            }

            // get the invitee list
            Bundle extras = data.getExtras();
            final ArrayList<String> invitees =
                    data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

            // get auto-match criteria
            Bundle autoMatchCriteria = null;
            int minAutoMatchPlayers =
                    data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
            int maxAutoMatchPlayers =
                    data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

            if (minAutoMatchPlayers > 0) {
                autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                        minAutoMatchPlayers, maxAutoMatchPlayers, 0);
            } else {
                autoMatchCriteria = null;
            }

            // create the room and specify a variant if appropriate
            RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
            roomConfigBuilder.addPlayersToInvite(invitees);
            if (autoMatchCriteria != null) {
                roomConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
            }
            RoomConfig roomConfig = roomConfigBuilder.build();
            Games.RealTimeMultiplayer.create(mGoogleApiClient, roomConfig);

            // prevent screen from sleeping during handshake
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    // create a RoomConfigBuilder that's appropriate for your implementation
    private RoomConfig.Builder makeBasicRoomConfigBuilder() {
        return RoomConfig.builder(this)
                .setMessageReceivedListener(this)
                .setRoomStatusUpdateListener(this);
    }

    private void startQuickGame() {
        // auto-match criteria to invite one random automatch opponent.
        // You can also specify more opponents (up to 3).
        Bundle am = RoomConfig.createAutoMatchCriteria(1, 1, 0);

        // build the room config:
        RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
        roomConfigBuilder.setAutoMatchCriteria(am);
        RoomConfig roomConfig = roomConfigBuilder.build();

        // create room:
        Games.RealTimeMultiplayer.create(mGoogleApiClient, roomConfig);

        // prevent screen from sleeping during handshake
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // go to game screen
    }

    @Override
    public void onRoomCreated(int statusCode, Room room) {
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            // let screen go to sleep
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            // show error message, return to main screen.
        }
    }

    @Override
    public void onJoinedRoom(int statusCode, Room room) {
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            // let screen go to sleep
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            // show error message, return to main screen.
        }
    }

    @Override
    public void onRoomConnected(int statusCode, Room room) {
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            // let screen go to sleep
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            // show error message, return to main screen.
        }
    }

    @Override
    public void onLeftRoom(int i, String s) {

    }

    @Override
    public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {

    }

    @Override
    public void onRoomConnecting(Room room) {

    }

    @Override
    public void onRoomAutoMatching(Room room) {

    }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> strings) {

    }

    @Override
    public void onPeerDeclined(Room room, List<String> peers) {
        // peer declined invitation -- see if game should be canceled
        if (!mPlaying && shouldCancelGame(room)) {
            Games.RealTimeMultiplayer.leave(mGoogleApiClient, null, mRoomId);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onPeerJoined(Room room, List<String> strings) {

    }

    @Override
    public void onPeerLeft(Room room, List<String> peers) {
        // peer left -- see if game should be canceled
        if (!mPlaying && shouldCancelGame(room)) {
            Games.RealTimeMultiplayer.leave(mGoogleApiClient, null, mRoomId);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onConnectedToRoom(Room room) {

    }

    @Override
    public void onDisconnectedFromRoom(Room room) {
        // leave the room
        Games.RealTimeMultiplayer.leave(mGoogleApiClient, null, mRoomId);

        // clear the flag that keeps the screen on
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // show error message and return to main screen
    }

    @Override
    public void onPeersConnected(Room room, List<String> peers) {
        if (mPlaying) {
            // add new player to an ongoing game
        } else if (shouldStartGame(room)) {
            // start game!
        }
    }

    @Override
    public void onPeersDisconnected(Room room, List<String> peers) {
        if (mPlaying) {
            // do game-specific handling of this -- remove player's avatar
            // from the screen, etc. If not enough players are left for
            // the game to go on, end the game and leave the room.
        } else if (shouldCancelGame(room)) {
            // cancel the game
            Games.RealTimeMultiplayer.leave(mGoogleApiClient, null, mRoomId);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void onP2PConnected(String s) {

    }

    @Override
    public void onP2PDisconnected(String s) {

    }

    // returns whether there are enough players to start the game
    boolean shouldStartGame(Room room) {
        int connectedPlayers = 0;
        for (Participant p : room.getParticipants()) {
            if (p.isConnectedToRoom()) ++connectedPlayers;
        }
        return connectedPlayers >= MIN_PLAYERS;
    }

    // Returns whether the room is in a state where the game should be canceled.
    boolean shouldCancelGame(Room room) {
        // TODO: Your game-specific cancellation logic here. For example, you might decide to
        // cancel the game if enough people have declined the invitation or left the room.
        // You can check a participant's status with Participant.getStatus().
        // (Also, your UI should have a Cancel button that cancels the game too)
        return false;
    }

    @Override
    public void onConnected(Bundle bundle) {
        System.out.println("GOOoGLE:ONCONNECTED");
        //Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, 1, 3);
        //startActivityForResult(intent, RC_SELECT_PLAYERS);
        Games.Invitations.registerInvitationListener(mGoogleApiClient, this);


    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("GOOoGLE:ONSUSPENDED");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        System.out.println("GOOoGLE:ONCONNECTIONFAILED");
    }

    @Override
    public void onInvitationReceived(Invitation invitation) {
        System.out.println("GOOoGLE:ONIRE");
    }

    @Override
    public void onInvitationRemoved(String s) {
        System.out.println("GOOoGLE:ONIRERE");
    }
}
