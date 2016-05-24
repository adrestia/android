package com.example.calvinkwan.college_confession;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Confessions_List_display extends AppCompatActivity implements View.OnClickListener {

    ArrayList<ConfessionOBJS> ArrayConfession = new ArrayList<ConfessionOBJS>();
    ListAdapter aa = null;
    ListView confessionsList;

    RadioGroup indexPosts;
    RadioButton newBttn, hotBttn, topBttn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confessions__list_display);

        indexPosts = (RadioGroup) findViewById(R.id.RadioGroup);
        newBttn = (RadioButton) findViewById(R.id.NewSelected);
        hotBttn = (RadioButton) findViewById(R.id.HotSelected);
        topBttn = (RadioButton) findViewById(R.id.TopSelect);

        newBttn.setOnClickListener(this);
        hotBttn.setOnClickListener(this);
        topBttn.setOnClickListener(this);
        indexPosts.check(topBttn.getId());          //sets default check mark on top posts

        grabJson("top");
    }

    @Override
    public void onClick(View v) {
        int checkRadioID = indexPosts.getCheckedRadioButtonId();

        switch (checkRadioID)
        {
            case R.id.NewSelected:
            {
                confessionsList.setAdapter(null);
                ArrayConfession.clear();
                grabJson("new");
            }
            break;
            case R.id.HotSelected:
            {
                confessionsList.setAdapter(null);
                ArrayConfession.clear();
                grabJson("hot");
            }
            break;
            case  R.id.TopSelect:
            {
                confessionsList.setAdapter(null);
                ArrayConfession.clear();
                grabJson("top");
            }
            break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.newConfession)
        {
            Intent postIntent = new Intent(getApplicationContext(), PostConfession.class);
            startActivity(postIntent);
            //will open new message activity

        }

        return super.onOptionsItemSelected(item);
    }


    void grabJson(String type)
    {

        SharedPreferences pref = getSharedPreferences("API_key", MODE_PRIVATE);
        String savedAPIKEY = pref.getString("api_KEY", null);
        if (savedAPIKEY != null)
        {
            final ProgressDialog loginUser = new ProgressDialog(this);
            loginUser.setTitle("Loading");
            loginUser.setMessage("Please wait");
            loginUser.show();
            String url;

            RequestQueue queue = Volley.newRequestQueue(this);
            url = "https://dev.collegeconfessions.party/api/" + type +"?apikey=" + savedAPIKEY;

            // Request a string response from the provided URL.
            final StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    loginUser.dismiss();

                    ParseJsonData(response);

                }
            },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                        }
                    });
            Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();
            queue.add(stringRequest);

        }
    }
    void ParseJsonData(String JSONobj)
    {
        if (JSONobj != null)
        {
            try
            {
                JSONArray array = new JSONArray(JSONobj);

                for (int i = 0; i < array.length(); i++)
                {
                    JSONObject obj = array.getJSONObject(i);

                    ConfessionOBJS confessionObject = new ConfessionOBJS();
                    if(obj.has("p_body")) {
                        confessionObject.body = obj.getString("p_body");
                    }
                    if(obj.has("p_created"))
                    {
                        confessionObject.p_created = obj.getString("p_created");
                    }
                    if(obj.has("comments"))
                    {
                        confessionObject.comments = obj.getInt("comments");
                    }
                    if(obj.has("l_voted"))
                    {
                        confessionObject.i_voted = obj.getString("l_voted");
                    }
                    if(obj.has("p_downvotes")) {
                        confessionObject.p_downvotes = obj.getInt("p_downvotes");
                    }
                    if(obj.has("p_id"))
                    {
                        confessionObject.p_id = obj.getInt("p_id");
                    }
                    if(obj.has("p_upvotes"))
                    {
                        confessionObject.p_upvotes = obj.getInt("p_upvotes");
                    }
                    if(obj.has("l_is_like"))
                    {
                        confessionObject.is_like = obj.getBoolean("l_is_like");
                    }

                    ArrayConfession.add(confessionObject);
                }

                confessionsList = (ListView) findViewById(R.id.confessionList);
                aa = new ListAdapter(this);
                confessionsList.setAdapter(aa);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



    public class ConfessionOBJS
    {
        int comments;               //number of commets
        Boolean is_like;            //if the individual liked the upvoted or downvoted
        String i_voted;             //timestamp of when they upvoted
        String body;                //body of confession
        String p_created;           //when post was created
        int p_downvotes;            //number of downvotes
        int p_id;                   //post ID
        int p_upvotes;              //number of upvotes
    }

    class ListAdapter extends BaseAdapter
    {
        Context context;

        ImageButton upvote = (ImageButton) findViewById(R.id.CommentUpvote);
        ImageButton downVote = (ImageButton) findViewById(R.id.CommentUpvote);



        ListAdapter(Context c)
        {
            context = c;

        }

        @Override
        public int getCount()
        {
            return ArrayConfession.size();
        }

        @Override
        public Object getItem(int position)
        {
            return ArrayConfession.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.complete_confession_layout, parent, false);
            final TextView body = (TextView) row.findViewById(R.id.body);
            final TextView time = (TextView) row.findViewById(R.id.minSincePost);
            final TextView comments = (TextView) row.findViewById(R.id.CommentButton);
            final TextView voteScore = (TextView) row.findViewById(R.id.CommentVoteScore);
            ImageButton report = (ImageButton) row.findViewById(R.id.CommentReport);
            final ImageButton Upvoting = (ImageButton) findViewById(R.id.CommentUpvote);
            ImageButton DownVoting = (ImageButton) findViewById(R.id.CommentDownvote);


            final ConfessionOBJS temp = ArrayConfession.get(position);
            body.setText(temp.body);
            //time.setText(temp.p_created);
            comments.setText("Comments: " + temp.comments);
            voteScore.setText(temp.p_upvotes - temp.p_downvotes + "");

            /*
            ((ImageButton)row.findViewById(R.id.reportPost)).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    SharedPreferences pref = getSharedPreferences("API_key", MODE_PRIVATE);
                    String savedAPIKEY = pref.getString("api_KEY", null);
                    if (savedAPIKEY != null) {
                        //Toast.makeText(getApplicationContext(), temp.body, Toast.LENGTH_LONG).show();
                        RequestQueue queue = Volley.newRequestQueue(context);
                        String url = "https://dev.collegeconfessions.party/api/posts/report?apikey=" + savedAPIKEY;

                        // Request a string response from the provided URL.
                        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response)
                            {
                                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                            }
                        },
                                new Response.ErrorListener()
                                {
                                    @Override
                                    public void onErrorResponse(VolleyError error)
                                    {
                                        //serverResponse.setText("Please make sure your credentials are valid");
                                    }
                                })

                        {;
                            @Override
                            public Map<String, String> getParams()
                            {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("post_id", 1 + "");
                                return params;
                            }

                            //https://gist.github.com/mombrea/7250835

                        };

                        // Add the request to the RequestQueue.
                        queue.add(stringRequest);
                    }
                }
            });

            */

            ((TextView)row.findViewById(R.id.CommentButton)).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(getApplicationContext(), CommentsActivity.class);
                    intent.putExtra("body", temp.body);
                    intent.putExtra("voteScore", voteScore.getText());
                    intent.putExtra("time", temp.p_created);
                    intent.putExtra("postID", temp.p_id);
                    startActivity(intent);
                }
            });

            ((ImageButton)row.findViewById(R.id.CommentUpvote)).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                   SharedPreferences pref = getSharedPreferences("API_key", MODE_PRIVATE);
                    String savedAPIKEY = pref.getString("api_KEY", null);
                    if (savedAPIKEY != null) {
                        //Toast.makeText(getApplicationContext(), temp.body, Toast.LENGTH_LONG).show();
                        RequestQueue queue = Volley.newRequestQueue(context);
                        String url = "https://dev.collegeconfessions.party/api/posts/upvote?apikey=" + savedAPIKEY;

                        // Request a string response from the provided URL.
                        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                                JSONObject responseOBJ = null;
                                try
                                {
                                    responseOBJ = new JSONObject(response);
                                    String score = responseOBJ.getString("score");
                                    voteScore.setText(score);
                                }
                                catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        },
                                new Response.ErrorListener()
                                {
                                    @Override
                                    public void onErrorResponse(VolleyError error)
                                    {
                                        //serverResponse.setText("Please make sure your credentials are valid");
                                    }
                                })

                        {
                            ;

                            @Override
                            public Map<String, String> getParams()
                            {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("post_id", temp.p_id + "");
                                return params;
                            }

                            //https://gist.github.com/mombrea/7250835

                        };

                        // Add the request to the RequestQueue.
                        queue.add(stringRequest);
                    }

                }
            });


            ((ImageButton)row.findViewById(R.id.CommentDownvote)).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    SharedPreferences pref = getSharedPreferences("API_key", MODE_PRIVATE);
                    String savedAPIKEY = pref.getString("api_KEY", null);
                    if (savedAPIKEY != null) {
                        //Toast.makeText(getApplicationContext(), temp.body, Toast.LENGTH_LONG).show();
                        RequestQueue queue = Volley.newRequestQueue(context);
                        String url = "https://dev.collegeconfessions.party/api/posts/downvote?apikey=" + savedAPIKEY;

                        // Request a string response from the provided URL.
                        final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response)
                            {
                                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                                JSONObject responseOBJ = null;
                                try
                                {
                                    responseOBJ = new JSONObject(response);
                                    String score = responseOBJ.getString("score");
                                    voteScore.setText(score);
                                }
                                catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        },
                                new Response.ErrorListener()
                                {
                                    @Override
                                    public void onErrorResponse(VolleyError error)
                                    {
                                        //serverResponse.setText("Please make sure your credentials are valid");
                                    }
                                })
                        {
                            ;

                            @Override
                            public Map<String, String> getParams()
                            {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("post_id", temp.p_id + "");
                                return params;
                            }

                            //https://gist.github.com/mombrea/7250835

                        };

                        // Add the request to the RequestQueue.
                        queue.add(stringRequest);
                    }

                }
            });

            return row;
        }

    }


}
