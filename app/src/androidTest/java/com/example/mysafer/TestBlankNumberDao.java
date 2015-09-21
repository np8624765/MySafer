package com.example.mysafer;

import android.content.Context;
import android.test.AndroidTestCase;

import com.example.mysafer.db.BlankNumberDao;

import java.util.Random;

/**
 * Created by Along on 2015/9/12.
 */
public class TestBlankNumberDao extends AndroidTestCase {

    public Context context;

    @Override
    protected void setUp() throws Exception {
        context = getContext();
        super.setUp();
    }

    public void testAdd() {
        BlankNumberDao dao = new BlankNumberDao(context);
        Random random = new Random();
        for(int i=0; i<50; i++){
            dao.add("130236547"+i, random.nextInt(3));
        }
    }



}
