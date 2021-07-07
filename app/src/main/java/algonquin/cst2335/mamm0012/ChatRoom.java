package algonquin.cst2335.mamm0012;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatRoom extends AppCompatActivity {
    RecyclerView chatList;
    ArrayList<ChatMessage> messages = new ArrayList<>();
    MyChatAdapter adt;
    SQLiteDatabase db;
    // Used for conversion between date as String and Date
    SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.chatlayout );

        chatList = findViewById(R.id.myrecycler);

        adt = new MyChatAdapter();
        chatList.setAdapter(adt);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        chatList.setLayoutManager(layoutManager);

        Button sendButton = findViewById(R.id.sendButton);
        Button receiveButton = findViewById(R.id.receiveButton);
        TextView messageTextView = findViewById(R.id.messageTextView);
        sendButton.setOnClickListener(clk -> {
            ChatMessage message = new ChatMessage(messageTextView.getText().toString(),1,new Date());

            ContentValues newRow = new ContentValues();
            newRow.put(MyOpenHelper.col_message, message.getMessage());
            newRow.put(MyOpenHelper.col_send_reveive, message.getSendOrReceive());
            newRow.put(MyOpenHelper.col_time_sent, message.getTimeSent());
            long newId = db.insert(MyOpenHelper.TABLE_NAME, MyOpenHelper.col_message, newRow);
            message.setId(newId);

            messages.add(message);
            messageTextView.setText("");

            adt.notifyItemInserted(messages.size() - 1);
        });
        receiveButton.setOnClickListener(clk -> {
            ChatMessage message = new ChatMessage(messageTextView.getText().toString(),2,new Date());

            ContentValues newRow = new ContentValues();
            newRow.put(MyOpenHelper.col_message, message.getMessage());
            newRow.put(MyOpenHelper.col_send_reveive, message.getSendOrReceive());
            newRow.put(MyOpenHelper.col_time_sent, message.getTimeSent());
            long newId = db.insert(MyOpenHelper.TABLE_NAME, MyOpenHelper.col_message, newRow);
            message.setId(newId);

            messages.add(message);
            messageTextView.setText("");

            adt.notifyItemInserted(messages.size() - 1);
        });

        MyOpenHelper opener = new MyOpenHelper(this);
        db = opener.getWritableDatabase();

        // Load messages from database
        Cursor results = db.rawQuery("Select * from " + MyOpenHelper.TABLE_NAME + ";", null);
        int _idCol = results.getColumnIndex("_id");
        int messageCol = results.getColumnIndex(MyOpenHelper.col_message);
        int sendCol = results.getColumnIndex(MyOpenHelper.col_send_reveive);
        int timeCol = results.getColumnIndex(MyOpenHelper.col_time_sent);

        while (results.moveToNext()) {
            long id = results.getInt(_idCol);
            String message = results.getString(messageCol);
            String time = results.getString(timeCol);
            int sendOrReceive = results.getInt(sendCol);
            try {
                Date timeAsDate = sdf.parse(time);
                messages.add(new ChatMessage(message, sendOrReceive, timeAsDate, id));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        results.close();
    }

    private class ChatMessage {
        String message;
        int sendOrReceive;
        Date timeSent;
        long id;

        public void setId(long l) { id = l;}
        public long getId() { return id;}

        public ChatMessage(String message, int sendOrReceive, Date timeSent) {
            this.message = message;
            this.sendOrReceive = sendOrReceive;
            this.timeSent = timeSent;
        }

        public ChatMessage(String message, int sendOrReceive, Date timeSent, long id) {
            this.message = message;
            this.sendOrReceive = sendOrReceive;
            this.timeSent = timeSent;
            setId(id);
        }

        public String getMessage() {
            return message;
        }

        public int getSendOrReceive() {
            return sendOrReceive;
        }

        public String getTimeSent() {
            return sdf.format(timeSent);
        }
    }

    private class MyRowViews extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timeText;
        int position = -1;

        public MyRowViews(View itemView) {
            super(itemView);

            itemView.setOnClickListener(click -> {
                AlertDialog.Builder builder = new AlertDialog.Builder( ChatRoom.this );
                builder.setMessage("Do you want to delete the message:" + messageText.getText())
                        .setTitle("Question:")
                        .setNegativeButton("No", (dialog, cl) -> {})
                        .setPositiveButton("Yes", (dialog, cl) -> {
                            position = getAbsoluteAdapterPosition();
                            ChatMessage removedMessage = messages.get(position);
                            messages.remove(position);
                            adt.notifyItemRemoved(position);

                            db.delete(MyOpenHelper.TABLE_NAME, "_id=?", new String[] {Long.toString(removedMessage.getId())});

                            Snackbar.make(messageText, "You deleted message #" + position, Snackbar.LENGTH_LONG)
                                    .setAction("Undo", clk -> {
                                        ContentValues newRow = new ContentValues();
                                        newRow.put(MyOpenHelper.col_message, removedMessage.getMessage());
                                        newRow.put(MyOpenHelper.col_send_reveive, removedMessage.getSendOrReceive());
                                        newRow.put(MyOpenHelper.col_time_sent, removedMessage.getTimeSent());
                                        long newId = db.insert(MyOpenHelper.TABLE_NAME, MyOpenHelper.col_message, newRow);
                                        removedMessage.setId(newId);

                                        messages.add(position, removedMessage);
                                        adt.notifyItemInserted(position);
                                    })
                                    .show();
                        })
                        .create().show();
            });

            messageText = itemView.findViewById(R.id.message);
            timeText = itemView.findViewById(R.id.time);
        }

        public void setPosition(int p) { position = p;}
    }

    private class MyChatAdapter extends RecyclerView.Adapter<MyRowViews> {
        @Override
        public MyRowViews onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            int layoutID;
            if (viewType == 1) // Send
                layoutID = R.layout.sent_message;
            else // Receive
                layoutID = R.layout.receive_message;
            View loadedRow = inflater.inflate(layoutID, parent, false);
            MyRowViews initRow = new MyRowViews(loadedRow);
            return initRow;
        }

        @Override
        public void onBindViewHolder(MyRowViews holder, int position) {
            holder.messageText.setText(messages.get(position).getMessage());
            holder.timeText.setText(messages.get(position).getTimeSent());
            holder.setPosition(position);
        }

        @Override
        public int getItemViewType(int position) {
            ChatMessage message = messages.get(position);
            return message.sendOrReceive;
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }
    }


}
