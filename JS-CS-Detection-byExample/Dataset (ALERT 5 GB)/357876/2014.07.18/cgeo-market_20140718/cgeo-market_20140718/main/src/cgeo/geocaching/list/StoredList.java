package cgeo.geocaching.list;

import cgeo.geocaching.CgeoApplication;
import cgeo.geocaching.DataStore;
import cgeo.geocaching.R;
import cgeo.geocaching.activity.ActivityMixin;
import cgeo.geocaching.ui.dialog.Dialogs;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNull;

import rx.functions.Action1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class StoredList extends AbstractList {
    public static final int TEMPORARY_LIST_ID = 0;
    public static final StoredList TEMPORARY_LIST = new StoredList(TEMPORARY_LIST_ID, "<temporary>", 0);  // Never displayed
    public static final int STANDARD_LIST_ID = 1;
    private final int count; // this value is only valid as long as the list is not changed by other database operations

    public StoredList(int id, String title, int count) {
        super(id, title);
        this.count = count;
    }

    @Override
    public String getTitleAndCount() {
        return title + " [" + count + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof StoredList)) {
            return false;
        }
        return id == ((StoredList) obj).id;
    }

    public static class UserInterface {
        private final Activity activity;
        private final CgeoApplication app;
        private final Resources res;

        public UserInterface(final Activity activity) {
            this.activity = activity;
            app = CgeoApplication.getInstance();
            res = app.getResources();
        }

        public void promptForListSelection(final int titleId, @NonNull final Action1<Integer> runAfterwards) {
            promptForListSelection(titleId, runAfterwards, false, -1);
        }

        public void promptForListSelection(final int titleId, @NonNull final Action1<Integer> runAfterwards, final boolean onlyConcreteLists, final int exceptListId) {
            promptForListSelection(titleId, runAfterwards, onlyConcreteLists, exceptListId, StringUtils.EMPTY);
        }

        public void promptForListSelection(final int titleId, @NonNull final Action1<Integer> runAfterwards, final boolean onlyConcreteLists, final int exceptListId, final String newListName) {
            final List<AbstractList> lists = new ArrayList<AbstractList>();
            lists.addAll(getSortedLists());

            if (exceptListId > StoredList.TEMPORARY_LIST_ID) {
                StoredList exceptList = DataStore.getList(exceptListId);
                if (exceptList != null) {
                    lists.remove(exceptList);
                }
            }

            if (!onlyConcreteLists) {
                if (exceptListId != PseudoList.ALL_LIST.id) {
                    lists.add(PseudoList.ALL_LIST);
                }
                if (exceptListId != PseudoList.HISTORY_LIST.id) {
                    lists.add(PseudoList.HISTORY_LIST);
                }
            }
            lists.add(PseudoList.NEW_LIST);

            final List<CharSequence> listsTitle = new ArrayList<CharSequence>();
            for (AbstractList list : lists) {
                listsTitle.add(list.getTitleAndCount());
            }

            final CharSequence[] items = new CharSequence[listsTitle.size()];

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(res.getString(titleId));
            builder.setItems(listsTitle.toArray(items), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int itemId) {
                    final AbstractList list = lists.get(itemId);
                    if (list == PseudoList.NEW_LIST) {
                        // create new list on the fly
                        promptForListCreation(runAfterwards, newListName);
                    }
                    else {
                        runAfterwards.call(lists.get(itemId).id);
                    }
                }
            });
            builder.create().show();
        }

        @NonNull
        private static List<StoredList> getSortedLists() {
            final Collator collator = Collator.getInstance();
            final List<StoredList> lists = DataStore.getLists();
            Collections.sort(lists, new Comparator<StoredList>() {

                @Override
                public int compare(StoredList lhs, StoredList rhs) {
                    // have the standard list at the top
                    if (lhs.id == STANDARD_LIST_ID) {
                        return -1;
                    }
                    if (rhs.id == STANDARD_LIST_ID) {
                        return 1;
                    }
                    // otherwise sort alphabetical
                    return collator.compare(lhs.getTitle(), rhs.getTitle());
                }
            });
            return lists;
        }

        public void promptForListCreation(@NonNull final Action1<Integer> runAfterwards, String newListName) {
            handleListNameInput(newListName, R.string.list_dialog_create_title, R.string.list_dialog_create, new Action1<String>() {

                // We need to update the list cache by creating a new StoredList object here.
                @SuppressWarnings("unused")
                @Override
                public void call(final String listName) {
                    final int newId = DataStore.createList(listName);
                    new StoredList(newId, listName, 0);

                    if (newId >= DataStore.customListIdOffset) {
                        ActivityMixin.showToast(activity, res.getString(R.string.list_dialog_create_ok));
                        runAfterwards.call(newId);
                    } else {
                        ActivityMixin.showToast(activity, res.getString(R.string.list_dialog_create_err));
                    }
                }
            });
        }

        private void handleListNameInput(final String defaultValue, int dialogTitle, int buttonTitle, final Action1<String> runnable) {
            Dialogs.input(activity, dialogTitle, defaultValue, buttonTitle, new Action1<String>() {

                @Override
                public void call(final String input) {
                    // remove whitespaces added by autocompletion of Android keyboard
                    String listName = StringUtils.trim(input);
                    if (StringUtils.isNotBlank(listName)) {
                        runnable.call(listName);
                    }
                }
            });
        }

        public void promptForListRename(final int listId, @NonNull final Runnable runAfterRename) {
            final StoredList list = DataStore.getList(listId);
            handleListNameInput(list.title, R.string.list_dialog_rename_title, R.string.list_dialog_rename, new Action1<String>() {

                @Override
                public void call(final String listName) {
                    DataStore.renameList(listId, listName);
                    runAfterRename.run();
                }
            });
        }

    }

    /**
     * Get the list title. This method is not public by intention to make clients use the {@link UserInterface} class.
     *
     * @return
     */
    protected String getTitle() {
        return title;
    }

    /**
     * Return the given list, if it is a concrete list. Return the default list otherwise.
     */
    public static int getConcreteList(int listId) {
        if (listId == PseudoList.ALL_LIST.id || listId == TEMPORARY_LIST_ID || listId == PseudoList.HISTORY_LIST.id) {
            return STANDARD_LIST_ID;
        }
        return listId;
    }

    @Override
    public boolean isConcrete() {
        return true;
    }

}
