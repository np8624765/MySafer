package com.example.mysafer.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mysafer.R;
import com.example.mysafer.bean.Contacts;

import java.util.ArrayList;
import java.util.List;

//选择联系人界面
public class ContactsActivity extends Activity {

    private ListView lv;
    private List<Contacts> contacts;

    class ContactsAdapter extends BaseAdapter {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Contacts person = contacts.get(position);
            View view = null;
            view =  View.inflate(ContactsActivity.this, R.layout.contacts_list_item, null);
//            if(convertView==null) {
//                view =  View.inflate(ContactsActivity.this, R.layout.contacts_list_item, null);
//            }else{
//                view = convertView;
//            }
            TextView tvName = (TextView)view.findViewById(R.id.tv_name);
            TextView tvPhone = (TextView)view.findViewById(R.id.tv_phone);
            tvName.setText(person.getName());
            tvPhone.setText(person.getPhone());
            return view;
        }
        @Override
        public int getCount() {
            return contacts.size();
        }

        @Override
        public Object getItem(int position) {
            return contacts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        contacts = new ArrayList<Contacts>();
        //获取系统联系人名单
        readContacts();

        lv = (ListView)findViewById(R.id.lv_contacts);
        lv.setAdapter(new ContactsAdapter());

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phone = ((TextView) view.findViewById(R.id.tv_phone)).getText().toString();
                Intent data = new Intent();
                data.putExtra("phone", phone);
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        });
    }

    //使用内容提供者去读取手机联系人,首先从raw_contacts表中读取出id，再通过data表读取全部数据，最后按照
    // mimetype表判断数据类型，内存提供者已经省去了最后一步的mimetype表查询，直接在第二步中给出了类型信息
    private void readContacts(){
        //需要查两个表，所以要有两个Uri
        Uri rawContactsUri = Uri.parse("content://com.android.contacts/raw_contacts");
        //实际上内容提供者是去查找view_data视图
        Uri dataUri = Uri.parse("content://com.android.contacts/data");
        Cursor contactsCursor = getContentResolver().query(
                rawContactsUri, new String[]{"contact_id"}, "account_id!=? and account_id!=?",
                new String[]{"13", "16"}, null);    //这里的过滤条件是为了过滤掉真机中的微博和微信同步的联系人
        if(contactsCursor!=null) {
            while(contactsCursor.moveToNext()) {
                String contactId = contactsCursor.getInt(0)+"";
                Cursor InfoCursor =  getContentResolver().query(dataUri,
                        new String[]{"data1", "mimetype"}, "contact_id=?", new String[]{contactId}, null);
                if(InfoCursor!=null) {
                    Contacts person = new Contacts();
                    while(InfoCursor.moveToNext()) {
                        String data1 = InfoCursor.getString(0);
                        String mimetype = InfoCursor.getString(1);
                        if(mimetype.equals("vnd.android.cursor.item/name")) {
                            person.setName(data1);
                        }
                        if(mimetype.equals("vnd.android.cursor.item/phone_v2")) {
                            person.setPhone(data1);
                        }
                    }
                    contacts.add(person);
                    InfoCursor.close();
                }

            }
            contactsCursor.close();
        }
    }


}
