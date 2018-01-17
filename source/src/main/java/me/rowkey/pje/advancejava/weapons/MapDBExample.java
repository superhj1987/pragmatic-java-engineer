package me.rowkey.pje.advancejava.weapons;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

import java.util.concurrent.TimeUnit;

/**
 * Created by Bryant.Hang on 2018/1/17.
 */
public class MapDBExample {
    public static void main(String[] args) {
        DB db = DBMaker.memoryDB().make();

        HTreeMap diskCache = db.hashMap("testCache")
                .expireStoreSize(10 * 1024)
                .expireMaxSize(1000)
                .expireAfterCreate(10, TimeUnit.SECONDS)
                .createOrOpen();

        HTreeMap cache = db.hashMap("testCache")
                .expireMaxSize(100)
                .expireOverflow(diskCache)
                .createOrOpen();
    }
}
