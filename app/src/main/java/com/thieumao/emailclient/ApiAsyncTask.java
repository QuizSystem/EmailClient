package com.thieumao.emailclient;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An asynchronous task that handles the Gmail API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class ApiAsyncTask extends AsyncTask<Void, Void, Void> {
    private MainActivity mActivity;
    private GoogleAccountCredential credential;

    private static final String USER_ID = "wtptester1@gmail.com";
    private static final String TO = "wtptester1@gmail.com";
    private static final String FROM = "wtptester1@gmail.com";

    /**
     * Constructor.
     * @param activity MainActivity that spawned this task.
     */
    ApiAsyncTask(MainActivity activity, GoogleAccountCredential credential) {
        this.mActivity = activity;
        this.credential = credential;
    }

    /**
     * Background task to call Gmail API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected Void doInBackground(Void... params) {

        /*
        try {
            String token = credential.getToken();
            Log.d("CredentialTask", "token:\n" + token);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GoogleAuthException e) {
            e.printStackTrace();
        }
        */

        try {
            mActivity.clearResultsText();
            //mActivity.updateResultsText(getDataFromApi());

            //GmailUtil.listAllMessages(mActivity.mService, USER_ID);

            List<Message> messages = listAllMessages(USER_ID, "", "");

            //asm2
            GmailUtil.sendMessage(mActivity.mService, USER_ID, GmailUtil.createEmail(TO, FROM, "Message from PRM assignment ----789---> FUNIX", "KoolJ send a message to FUNIX xters ----------------------> at " + System.currentTimeMillis()+"."));
            if(messages != null && messages.size() > 0){
                //GmailUtil.listAllMessages(mActivity.mService, USER_ID);
                GmailUtil.getMessage(mActivity.mService, USER_ID, messages.get(1).getId(), "raw");
                //GmailUtil.getDataFromApi();
                //GmailUtil.listAllMessages(mActivity.mService, USER_ID);
            }
            //asm 3
            //GmailUtil.getMessage(mActivity.mService, USER_ID, messages.get(0).getId(), "inbox");
            //mActivity.updateStatus(GmailUtil.listAllMessages(mActivity.mService, USER_ID));
            //GmailUtil.listAllMessages(mActivity.mService, USER_ID);

        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
            mActivity.showGooglePlayServicesAvailabilityErrorDialog(
                    availabilityException.getConnectionStatusCode());

        } catch (UserRecoverableAuthIOException userRecoverableException) {
            mActivity.startActivityForResult(
                    userRecoverableException.getIntent(),
                    MainActivity.REQUEST_AUTHORIZATION);

        } catch (Exception e) {
            mActivity.updateStatus("The following error occurred:\n" +
                    e.getMessage());
        }
        return null;
    }

    /**
     * Fetch a list of Gmail labels attached to the specified account.
     * @return List of Strings labels.
     * @throws IOException
     */
    private List<String> getDataFromApi() throws IOException {
        // Get the labels in the user's account.
        String user = "me";
        List<String> labels = new ArrayList<String>();
        ListLabelsResponse listResponse =
                mActivity.mService.users().labels().list(user).execute();
        for (Label label : listResponse.getLabels()) {
            labels.add(label.getName());
            System.out.println("======LIST NAME============================================>");
        }
        return labels;
    }

    /**
     * List all Messages of the user's mailbox with labelIds applied.
     *
     * @param userId User's email address. The special value "me"
     * can be used to indicate the authenticated user.
     * @throws IOException
     */
    public List<Message> listAllMessages(String userId, String messageId, String format) throws IOException {
        System.out.println("listMessagesWithLabels");
        Message message2 = null;
        ListMessagesResponse response = mActivity.mService.users().messages().list(userId).execute();
        message2 = mActivity.mService.users().messages().get(userId, messageId).setFormat(format).execute();

        List<Message> messages = new ArrayList<Message>();
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = mActivity.mService.users().messages().list(userId)
                        .setPageToken(pageToken).execute();
            } else {
                break;
            }
        }
        int i = 0;
        for (Message message : messages) {

            System.out.println(message.toPrettyString());
            GmailUtil.getMessage(mActivity.mService, USER_ID, messages.get(i).getId(), "raw");
            System.out.println("=============LIST MESSAGES ======================================>\n");
            i += 1;
        }

        return messages;
    }
}
