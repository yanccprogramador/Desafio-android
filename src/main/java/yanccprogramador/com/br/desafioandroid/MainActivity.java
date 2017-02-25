package yanccprogramador.com.br.desafioandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.RequiresApi;
import android.text.Html;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.id;
import static android.R.attr.textSize;
import static com.android.volley.Request.Method.*;
import static java.security.AccessController.getContext;

public class MainActivity extends Activity  {
    private static String TAG = "MA";
    public String getURL = "https://api.github.com/search/repositories?q=language:Java&sort=stars&per_page=100&page=0";
    public String pullURL = "https://api.github.com/repos/";
    private ArrayList<Spanned> lista, listaPull;
    private List<String> urls, links;
    private ArrayAdapter<Spanned> adp, adpPull;
    private RequestQueue mRequestQueue;
    private JsonObjectRequest req;
    private JsonArrayRequest req1;
    String html;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    NetworkImageView niv;

    // temporary string to show the parsed response


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lista = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            makeJsonArrayRequest();
            getURL = getURL.replace("&page=" + (i - 1), "&page=" + (i));
        }
        niv=new NetworkImageView(getApplicationContext());

        adp = new ArrayAdapter<Spanned>(this, android.R.layout.simple_list_item_1, lista);
        urls = new ArrayList<>();
        setContentView(R.layout.repos);
        ListView lv = (ListView) findViewById(R.id.lv1);
        lv.setAdapter(adp);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), urls.get(i), Toast.LENGTH_SHORT).show();
                pullURL = "https://api.github.com/repos/" + urls.get(i);
                listaPull = new ArrayList<>();
                links = new ArrayList<>();
                buscarPuls();
                setContentView(R.layout.pulls);
                ListView lv1 = (ListView) findViewById(R.id.lv2);
                adpPull = new ArrayAdapter<Spanned>(getApplicationContext(), android.R.layout.simple_list_item_1, listaPull);
                lv1.setAdapter(adpPull);

                lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (!listaPull.get(0).equals("Não existe pulls")) {
                            Uri uri = Uri.parse(links.get(i));

                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                            startActivity(intent);
                        }
                    }
                });
            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    private void makeJsonArrayRequest() {


        req = new JsonObjectRequest(GET, getURL, null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onResponse(JSONObject response) {
                        String login = "";
                        String name = "";
                        try {

                            Log.i(TAG, response.toString());
                            // Parsing json array response
                            // loop through each json object


                            for (int i = 0; i < 100; i++) {
                                JSONArray js = response.getJSONArray("items");

                                JSONObject items = js.getJSONObject(i);
                                name = items.getString("name");
                                String descricao = items.getString("description");
                                String forks = items.getString("forks_count");
                                String stars = items.getString("stargazers_count");
                                JSONObject owner = items.getJSONObject("owner");
                                login = owner.getString("login");
                                String foto = owner.getString("avatar_url");

                                niv.setImageUrl(foto, VolleySingleton.getInstance(MainActivity.this).getImageLoader());
                                String html = "<html><body><img src=\"" + niv.getDrawable()+ "\" />" +
                                        " Dono: " + login + " <br/>Repositorio :" + name + "<br/>descrição:" + descricao + "<br/> " +
                                        "forks: " + forks + "  Stars: " + stars + "</body></html>";

                                lista.add(i,Html.fromHtml(html,new ImageGetter(niv.getDrawable()),null));
                                urls.add(i, login + "/" + name + "/pulls");


                            }

                            finalize();
                            Toast.makeText(getApplicationContext(), "Busca Finalizada com sucesso!", Toast.LENGTH_SHORT).show();
                            adp.addAll(lista);
                            Log.i("Fim", "Sucesso");
                            final String finalLogin = login;
                            final String finalName = name;

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error, tente novamente",
                                    Toast.LENGTH_LONG).show();
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();

                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Erro tente Novamente", Toast.LENGTH_SHORT).show();

            }
        });
        addToRequestQueue(req);
    }

    private void buscarPuls() {

        req1 = new JsonArrayRequest(GET, pullURL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {


                        try {
                            Log.i(TAG, response.toString());
                            String link = "";
                            // Parsing json array response
                            // loop through each json object
                            if (response.length() > 0) {
                                for (int i = 0; i < response.length(); i++) {

                                    JSONObject person = response.getJSONObject(i);
                                    String name = person.getString("title");
                                    String descricao = person.getString("body");
                                    String data = person.getString("created_at");
                                    link = person.getString("html_url");
                                    JSONObject owner = person
                                            .getJSONObject("user");
                                    String login = owner.getString("login");
                                    String foto = owner.getString("avatar_url");

                                    html = "Nome pull: " + name + "<br/>Descrição: " + descricao + "<br/>Criado em:" + data + "<br/><img url='" + foto + "' style=\"#img{width:20px;heigth:20px;}\"/>  Dono:" + login;

                                    listaPull.add(i,Html.fromHtml(html));
                                    links.add(i, link);
                                }
                                finalize();
                                Toast.makeText(getApplicationContext(), "Pulls encontrado com sucesso!", Toast.LENGTH_SHORT).show();
                                adpPull.addAll(listaPull);


                            } else {
                                listaPull.add(0,Html.fromHtml("<b>Não existe pulls</b>"));
                                adpPull.addAll(listaPull);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        addToRequestQueue(req1);

    }

    public RequestQueue getRequestQueue() {

        mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }


}
class ImageGetter implements Html.ImageGetter {
    Drawable dr;
    public ImageGetter(Drawable d){
        dr=d;
        getDrawable("");
    }

    public Drawable getDrawable1(Drawable d) {
        int id;

        return d;
    }

    @Override
    public Drawable getDrawable(String s) {
        return getDrawable1(dr);
    }
};