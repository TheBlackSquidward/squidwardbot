package io.github.theblacksquidward.destinyapiwrapper;

import com.google.gson.Gson;
import io.github.theblacksquidward.destinyapiwrapper.utils.UnzipUtils;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class DestinyAPI {

    public static final Gson GSON = new Gson();
    private static final File CACHE_DIR = new File("cache");

    public static void main(final String[] args) {
        //init();

        var string = "0xffffff";

        System.out.println(Long.decode(string));

    }

    public static void init() {
        //TODO
        clearCache();
        createCache();
        var dict = getDict();

        var from = ManifestHandler.getManifestDatabaseURL();
        var to = "cache/MANIFEST.zip";
        try {
            downloadManifestZip(from, to);
            UnzipUtils.unzip(to, "cache/");
        } catch (Exception e) {
            e.printStackTrace();
        }
        var databaseConnection = connectDatabase();
        if(databaseConnection != null) {
            System.out.println("Connection to SQLite has been established.");
        }

        dict.forEach((key, hash) -> {
            var string = "SELECT json from "+ key;
            System.out.println(string);
            try (PreparedStatement preparedStatement = databaseConnection.prepareStatement(string)) {
               // var idek = preparedStatement.executeQuery();
                var idek2 = preparedStatement.getResultSet();
                var idek3 = "test";
                System.out.println(idek3);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


    }

    public static Map<String, String> getDict() {
        Map<String, String> map = new HashMap<>();
        map.put("DestinyInventoryItemDefinition", "itemHash");
        return map;
    }

    /**
     * Helper method that will delete the cache folder and therefore clear its contents.
     */
    public static void clearCache() {
        try {
            Files.walk(CACHE_DIR.toPath())
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO rename

    /**
     * A helper method that will create the cache directory used to store any data that requires caching locally.
     */
    public static void createCache() {
        CACHE_DIR.mkdir();
    }

    /**
     * This method downloads the zip.
     * @param from
     * @param to
     * @throws IOException
     */
    public static void downloadManifestZip(String from, String to) throws IOException {
        URL url = new URL(from);

        HttpURLConnection conn;
        while (true) {
            conn = (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(true);

            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_MOVED_PERM &&
                    responseCode != HttpURLConnection.HTTP_MOVED_TEMP &&
                    responseCode != 307) {

                break;
            }

            url = new URL(conn.getHeaderField("Location"));
        }

        try (InputStream stream = conn.getInputStream()) {
            Files.copy(stream, Paths.get(to));
        }
    }

    @Nullable
    public static Connection connectDatabase() {
        Connection connection = null;
        try {
            var test = new File("cache/world_sql_content_3d029e66883b2c5765b6e4848f1c2965.content");
            var abspath = test.getAbsolutePath();
            //connection = DriverManager.getConnection("jdbc:sqlite:" + abspath);
            connection = DriverManager.getConnection("jdbc:sqlite:E:/Coding/Java/DestinyBot/cache/world_sql_content_3d029e66883b2c5765b6e4848f1c2965.content");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }



}
