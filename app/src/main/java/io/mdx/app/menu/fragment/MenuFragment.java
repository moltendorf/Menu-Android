package io.mdx.app.menu.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.moltendorf.android.recyclerviewadapter.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import io.mdx.app.menu.R;
import io.mdx.app.menu.model.Menu;
import io.mdx.app.menu.model.MenuSection;
import io.mdx.app.menu.network.Backend;
import io.mdx.app.menu.viewholder.MenuItemViewHolder;
import io.mdx.app.menu.viewholder.MenuSectionViewHolder;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by moltendorf on 16/4/29.
 */
public class MenuFragment extends BaseFragment {
  public static final String ACTION_MENU = "io.mdx.app.menu.MENU";

  private static FragmentType TYPE = FragmentType.MENU;

  private List data = new ArrayList();

  private RecyclerViewAdapter menuAdapter;
  private RecyclerView        list;

  public MenuFragment() {
    super(TYPE, R.layout.fragment_menu);

    fetchMenu();
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    createAdapter();
    setupList();
  }

  private void createAdapter() {
    menuAdapter = new RecyclerViewAdapter(getContext());
    menuAdapter.setViewHolders(MenuSectionViewHolder.class, MenuItemViewHolder.class);
    menuAdapter.changeDataSet(data);
  }

  private void fetchMenu() {
    Observable<Menu> observable = Backend.getService().getMenu();

    observable.subscribeOn(Schedulers.newThread())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(new Subscriber<Menu>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
          Timber.e(e.getMessage());
        }

        @Override
        public void onNext(Menu menu) {
          data.clear();

          for (MenuSection section : menu.getSections()) {
            data.add(section);
            data.addAll(section.getItems());
          }

          if (menuAdapter != null) {
            menuAdapter.changeDataSet(data);
          }
        }
      });
  }

  private void setupList() {
    list = (RecyclerView) getView();
    list.setLayoutManager(new LinearLayoutManager(getContext()));
    list.setAdapter(menuAdapter);
  }

  public static class Factory implements FragmentFactory<MenuFragment> {
    @Override
    public FragmentType getType() {
      return TYPE;
    }

    @Override
    public MenuFragment newInstance() {
      return new MenuFragment();
    }
  }
}
