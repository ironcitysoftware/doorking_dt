/**
 * Copyright 2018 Iron City Software LLC
 *
 * This file is part of DoorKing.
 *
 * DoorKing is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DoorKing is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DoorKing.  If not, see <http://www.gnu.org/licenses/>.
 */

package doorking;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import doorking.Proto.Config;

public class GoogleRetriever {
  private final Config config;
  private final File dataStoreDirectory;
  private final DataStoreFactory dataStoreFactory;
  private final HttpTransport httpTransport;
  private final JsonFactory jsonFactory;
  private final Logger logger = Logger.getLogger(GoogleRetriever.class.getName());

  public GoogleRetriever(Config config) throws IOException, GeneralSecurityException {
    this.config = config;
    this.dataStoreDirectory = new File(config.getDataStoreDirectory());
    this.dataStoreFactory = new FileDataStoreFactory(dataStoreDirectory);
    this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    this.jsonFactory = new JacksonFactory();
  }

  public static class Result {
    public final List<List<Object>> entries;
    public final List<List<Object>> codes;
    public final List<List<Object>> deletedCodes;
    
    public Result(List<List<Object>> entries, List<List<Object>> codes,
        List<List<Object>> deletedCodes) {
      this.entries = entries;
      this.codes = codes;
      this.deletedCodes = deletedCodes;
    }
  }

  public Result retrieve() throws Exception {
    Credential credential = authorize();
    logger.info("Authorized with token: " + credential.getAccessToken());
    Sheets service = new Sheets.Builder(httpTransport, jsonFactory, credential)
        .setApplicationName(config.getApplicationName())
        .build();
    ValueRange entries = service.spreadsheets().values()
        .get(config.getSheetId(), config.getTelephoneEntryRange())
        .execute();
    ValueRange codes = service.spreadsheets().values()
        .get(config.getSheetId(), config.getEntryCodeRange())
        .execute();
    ValueRange deletedCodes = service.spreadsheets().values()
        .get(config.getSheetId(), config.getDeletedEntryCodeRange())
        .execute();
    return new Result(entries.getValues(), codes.getValues(), deletedCodes.getValues());
  }

  /** Authorizes the installed application to access user's protected data. */
  private Credential authorize() throws Exception {
    logger.info("Authorizing with LocalServerReceiver");
    AuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport,
        jsonFactory,
        config.getClientId(),
        config.getClientSecret(),
        Collections.singleton(SheetsScopes.SPREADSHEETS_READONLY))
            .setDataStoreFactory(dataStoreFactory)
            .build();
    final LocalServerReceiver receiver = new LocalServerReceiver.Builder().build();
    AuthorizationCodeInstalledApp app = new AuthorizationCodeInstalledApp(flow, receiver) {
      @Override
      protected void onAuthorization(AuthorizationCodeRequestUrl authorizationUrl) throws IOException {
        String url = authorizationUrl.build();
        new ProcessBuilder("C:\\Program Files\\Mozilla Firefox\\firefox.exe", "-new-window", url).start();
      }
    };
    return app.authorize(config.getGoogleUsername());
  }
}