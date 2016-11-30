/*
 * jPOS Project [http://jpos.org]
 * Copyright (C) 2000-2010 Alejandro P. Revilla
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jpos.space;

import java.io.*;
import java.util.Map;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import com.sleepycat.je.*;
import com.sleepycat.persist.EntityStore; 
import com.sleepycat.persist.StoreConfig; 
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;
import com.sleepycat.persist.model.Relationship;

import org.jpos.util.Log;
import org.jpos.util.DefaultTimer;
import org.jpos.util.Loggeable;

/**
 * BerkeleyDB Jave Edition based persistent space implementation
 *
 * @author Alejandro Revilla
 * @since 1.6.5
 */
public class JESpace<K,V> extends Log implements Space<K,V>, Loggeable {
    Environment dbe = null;
    EntityStore store = null;
    PrimaryIndex<Long, Ref> pIndex = null;
    PrimaryIndex<Long,GCRef> gcpIndex = null;
    SecondaryIndex<String,Long, Ref> sIndex = null;
    SecondaryIndex<Long,Long,GCRef> gcsIndex = null;
    Semaphore gcSem = new Semaphore(1);
    TimerTask gcTimerTask = null;
    public static final long GC_DELAY = 5*60*1000L;

    static final Map<String,Space> spaceRegistrar = 
        new HashMap<String,Space> ();

    public JESpace(String name, String path) throws SpaceError {
        super();
        try {
            EnvironmentConfig envConfig = new EnvironmentConfig();
            StoreConfig storeConfig = new StoreConfig();

            envConfig.setAllowCreate (true);
            envConfig.setTransactional (true);
            envConfig.setTxnTimeout(30000000); // 30 seconds
            storeConfig.setAllowCreate (true);
            storeConfig.setTransactional (true);

            File dir = new File(path);
            dir.mkdirs();

            dbe = new Environment (dir, envConfig);
            store = new EntityStore (dbe, name, storeConfig);
            pIndex = store.getPrimaryIndex (Long.class, Ref.class);
            gcpIndex = store.getPrimaryIndex (Long.class, GCRef.class);
            sIndex = store.getSecondaryIndex (pIndex, String.class, "key");
            gcsIndex = store.getSecondaryIndex (gcpIndex, Long.class, "expires");
            scheduleGC();
        } catch (Exception e) {
            throw new SpaceError (e);
        }
    }

    public void out (K key, V value) {
        out (key, value, 0L);
    }
    public void out (K key, V value, long timeout) {
        Transaction txn = null;
        try {
            txn = dbe.beginTransaction (null, null);
            Ref ref = new Ref(key.toString(), value, timeout);
            pIndex.put (ref);
            if (timeout > 0L)
                gcpIndex.putNoReturn (
                    new GCRef (ref.getId(), ref.getExpiration())
                );
            txn.commit();
            txn = null;
            synchronized (this) {
                notifyAll ();
            }
        } catch (Exception e) {
            throw new SpaceError (e);
        } finally {
            if (txn != null)
                abort (txn);
        }
    }
    public void push (K key, V value, long timeout) {
        Transaction txn = null;
        try {
            txn = dbe.beginTransaction (null, null);
            Ref ref = new Ref(key.toString(), value, timeout);
            pIndex.put (ref);
            pIndex.delete (ref.getId());
            ref.reverseId();
            pIndex.put (ref);
            txn.commit();
            txn = null;
            synchronized (this) {
                notifyAll ();
            }
        } catch (Exception e) {
            throw new SpaceError (e);
        } finally {
            if (txn != null)
                abort (txn);
        }
    }
    public void push (K key, V value) {
        push (key, value, 0L);
    }
    @SuppressWarnings("unchecked")
    public V rdp (Object key) {
        try {
            return (V) getObject (key, false);
        } catch (DatabaseException e) {
            throw new SpaceError (e);
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized V in (Object key) {
        Object obj;
        while ((obj = inp (key)) == null) {
            try {
                this.wait ();
            } catch (InterruptedException ignored) { }
        }
        return (V) obj;
    }
    @SuppressWarnings("unchecked")
    public synchronized V in (Object key, long timeout) {
        Object obj;
        long now = System.currentTimeMillis();
        long end = now + timeout;
        while ((obj = inp (key)) == null && 
                ((now = System.currentTimeMillis()) < end))
        {
            try {
                this.wait (end - now);
            } catch (InterruptedException ignored) { }
        }
        return (V) obj;
    }

    @SuppressWarnings("unchecked")
    public synchronized V rd  (Object key) {
        Object obj;
        while ((obj = rdp (key)) == null) {
            try {
                this.wait ();
            } catch (InterruptedException ignored) { }
        }
        return (V) obj;
    }
    @SuppressWarnings("unchecked")
    public synchronized V rd  (Object key, long timeout) {
        Object obj;
        long now = System.currentTimeMillis();
        long end = now + timeout;
        while ((obj = rdp (key)) == null && 
                ((now = System.currentTimeMillis()) < end))
        {
            try {
                this.wait (end - now);
            } catch (InterruptedException ignored) { }
        }
        return (V) obj;
    }

    @SuppressWarnings("unchecked")
    public V inp (Object key) {
        try {
            return (V) getObject (key, true);
        } catch (DatabaseException e) {
            throw new SpaceError (e);
        }
    }

    public boolean existAny (Object[] keys) {
        for (Object key : keys) {
            if (rdp(key) != null) {
                return true;
            }
        }
        return false;
    }
    public boolean existAny (Object[] keys, long timeout) {
        long now = System.currentTimeMillis();
        long end = now + timeout;
        while (((now = System.currentTimeMillis()) < end)) {
            if (existAny (keys))
                return true;
            synchronized (this) {
                try {
                    wait (end - now);
                } catch (InterruptedException ignored) { }
            }
        }
        return false;
    }
    public synchronized void put (K key, V value, long timeout) {
        while (inp (key) != null)
            ;
        out (key, value, timeout);
    }
    public synchronized void put (K key, V value) {
        while (inp (key) != null)
            ;
        out (key, value);
    }
    public void gc () throws DatabaseException {
        Transaction txn = null;
        EntityCursor<GCRef> cursor = null;
        try {
            if (!gcSem.tryAcquire())
                return;
            txn = dbe.beginTransaction (null, null);
            cursor = gcsIndex.entities (
                txn, 0L, true, System.currentTimeMillis(), false, null
            );
            for (GCRef gcRef: cursor) {
                pIndex.delete (gcRef.getId());
                cursor.delete ();
            }
            cursor.close();
            cursor = null;
            txn.commit();
            txn = null;
        } finally {
            if (cursor != null)
                cursor.close();
            if (txn != null)
                abort (txn);
            gcSem.release();
        }
    }
    public void scheduleGC() {
        gcTimerTask  = new TimerTask() {
            public void run() {
                try {
                    gc();
                } catch (DatabaseException e) {
                    warn (e);
                }
            }
        };
        DefaultTimer.getTimer().schedule(gcTimerTask, GC_DELAY, GC_DELAY);
    }
    public void close () throws DatabaseException {
        gcSem.acquireUninterruptibly();
        gcTimerTask.cancel();
        store.close ();
        dbe.close();
    }

    public synchronized static JESpace getSpace (String name, String path)
    {
        JESpace sp = (JESpace) spaceRegistrar.get (name);
        if (sp == null) {
            sp = new JESpace(name, path);
            spaceRegistrar.put (name, sp);
        }
        return sp;
    }
    public static JESpace getSpace (String name) {
        return getSpace (name, name);        
    }
    private Object getObject (Object key, boolean remove) throws DatabaseException {
        Transaction txn = null;
        EntityCursor<Ref> cursor = null;
        Template tmpl = null;
        if (key instanceof Template) {
            tmpl = (Template) key;
            key  = tmpl.getKey();
        }
        try {
            txn = dbe.beginTransaction (null, null);
            cursor = sIndex.subIndex(key.toString()).entities(txn, null);
            for (Ref ref : cursor) {
                if (ref.isActive()) {
                    if (tmpl != null && !tmpl.equals (ref.getValue()))
                        continue;
                    if (remove) {
                        cursor.delete();
                        if (ref.hasExpiration()) 
                            gcpIndex.delete (txn, ref.getId());
                    }
                    cursor.close(); cursor = null;
                    txn.commit(); txn = null;
                    return ref.getValue();
                }
                else {
                    cursor.delete();
                    if (ref.hasExpiration()) 
                        gcpIndex.delete (txn, ref.getId());
                }
            }
            cursor.close(); cursor = null;
            txn.commit(); txn = null;
            return null;
        } finally {
            if (cursor != null)
                cursor.close ();
            if (txn != null)
                txn.abort();
        }
    }
    private void abort (Transaction txn) throws SpaceError {
        try {
            txn.abort();
        } catch (DatabaseException e) {
            throw new SpaceError (e);
        }
    }

    @Entity
    public static class Ref {
        @PrimaryKey(sequence="Ref.id")
        private long id;

        @SecondaryKey(relate= Relationship.MANY_TO_ONE)
        private String key;

        private long expires;
        private Object value;

        public Ref () {
            super();
        }
        public Ref (String key, Object value, long timeout) {
            this.key = key;
            this.value =  serialize (value);
            if (timeout > 0L)
                this.expires = System.currentTimeMillis() + timeout;
        }
        public long getId() {
            return id;
        }
        public void reverseId() {
            this.id = -this.id;
        }
        public boolean isExpired () {
            return expires > 0L && expires < System.currentTimeMillis ();
        }
        public boolean isActive () {
            return !isExpired();
        }
        public Object getKey () {
            return key;
        }
        public Object getValue () {
            return deserialize(value);
        }
        public long getExpiration () {
            return expires;
        }
        public boolean hasExpiration () {
            return expires > 0L;
        }
        private boolean isPersistent (Class c) {
            return
                c.isPrimitive() ||
                c.isAnnotationPresent(Entity.class) ||
                c.isAnnotationPresent(Persistent.class);
        }
        private Object serialize (Object obj) {
            Class cls = obj.getClass();
            if (isPersistent (cls))
                return obj;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ObjectOutputStream os = new ObjectOutputStream(baos);
                os.writeObject(obj);
                obj = baos.toByteArray();
            } catch (IOException e) {
                throw new SpaceError (e);
            }
            return obj;
        }
        private Object deserialize (Object obj) {
            Class cls = obj.getClass();
            if (isPersistent (cls))
                return obj;

            ByteArrayInputStream bais = new ByteArrayInputStream((byte[]) obj);
            try {
                ObjectInputStream is = new ObjectInputStream (bais);
                return is.readObject();
            } catch (Exception e) {
                throw new SpaceError (e);
            }

        }
    }
    
    public void dump(PrintStream p, String indent) {
        Transaction txn = null;
        EntityCursor<Ref> cursor = null;
        int count = 0;
        try {
            txn = dbe.beginTransaction (null, null);
            cursor = sIndex.entities(txn, null);
            String key = null;
            int keyCount = 0;
            for (Ref ref : cursor) {
                if (ref.getKey().equals(key)) {
                    keyCount++;
                } else {
                    if (key != null) {
                        dumpKey (p, indent, key, keyCount);
                        count++;
                    }
                    keyCount = 1;
                    key = ref.getKey().toString();
                }
            }
            if (key != null) {
                dumpKey (p, indent, key, keyCount);
                count++;
            }
            p.println(indent+"<keycount>"+count+"</keycount>");
            cursor.close(); cursor = null;
            txn.commit(); txn = null;
        } catch (IllegalStateException e) {
            //Empty Cursor
            p.println(indent+"<keycount>0</keycount>");
        } finally {
            if (cursor != null)
                cursor.close ();
            if (txn != null)
                txn.abort();
        }
    }

    private void dumpKey (PrintStream p, String indent, String key, int count) {
        if (count > 0)
            p.printf ("%s<key size='%d'>%s</key>\n", indent, count, key);
        else
            p.printf ("%s<key>%s</key>\n", indent, key);
    }

    @Entity
    public static class GCRef {
        @PrimaryKey
        private long id;

        @SecondaryKey(relate=Relationship.MANY_TO_ONE)
        private long expires;
        public GCRef () {
            super();
        }
        public GCRef (long id, long expires) {
            this.id = id;
            this.expires = expires;
        }
        public long getId() {
            return id;
        }
        public boolean isExpired () {
            return expires > 0L && expires < System.currentTimeMillis ();
        }
        public long getExpiration () {
            return expires;
        }
    }
}
