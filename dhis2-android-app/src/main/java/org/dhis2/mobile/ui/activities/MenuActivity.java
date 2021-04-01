/*
 * Copyright (c) 2014, Araz Abishov
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.dhis2.mobile.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import org.dhis2.mobile.R;
import org.dhis2.mobile.WorkService;
import org.dhis2.mobile.ui.fragments.AboutUsFragment;
import org.dhis2.mobile.ui.fragments.AggregateReportFragment;
import org.dhis2.mobile.ui.fragments.MyProfileFragment;
import org.dhis2.mobile.ui.fragments.SyncLogFragment;
import org.dhis2.mobile.utils.ViewUtils;

public class MenuActivity extends BaseActivity implements OnNavigationItemSelectedListener {
    private static final String STATE_TOOLBAR_TITLE = "state:toolbarTitle";

    // layout
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        if (navigationView != null) {
            navigationView.inflateMenu(R.menu.menu_drawer);
            navigationView.setNavigationItemSelectedListener(this);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_menu);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleNavigationDrawer();
                }
            });
        }

        if (savedInstanceState == null) {
            onNavigationItemSelected(navigationView.getMenu()
                    .findItem(R.id.drawer_item_aggregate_report));
        } else if (savedInstanceState.containsKey(STATE_TOOLBAR_TITLE) && toolbar != null) {
            setTitle(savedInstanceState.getString(STATE_TOOLBAR_TITLE));
        }

        if(ViewUtils.fragmentSelected != null) {
            attachFragment(ViewUtils.fragmentSelected);
            setTitle(ViewUtils.title);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.drawer_item_aggregate_report: {
                ViewUtils.fragmentSelected = new AggregateReportFragment();
                attachFragment(ViewUtils.fragmentSelected);
                break;
            }
            case R.id.drawer_item_profile: {
                ViewUtils.fragmentSelected = new MyProfileFragment();
                attachFragment(ViewUtils.fragmentSelected);
                break;
            }
            case R.id.drawer_item_about: {
                ViewUtils.fragmentSelected = new AboutUsFragment();
                attachFragment(ViewUtils.fragmentSelected);

                break;
            }
            case R.id.drawer_item_sync_logs: {
                ViewUtils.fragmentSelected = new SyncLogFragment();
                attachFragment(ViewUtils.fragmentSelected);
                break;
            }
            case R.id.drawer_item_logout: {
                new AlertDialog.Builder(this)
                        .setTitle(getApplicationContext().getString(R.string.log_out))
                        .setMessage(R.string.dialog_content_logout_confirmation)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                logOut();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).create().show();
                break;
            }
        }

        setTitle(item.getTitle());
        ViewUtils.title = item.getTitle().toString();
        navigationView.setCheckedItem(R.id.drawer_item_aggregate_report);
        drawerLayout.closeDrawers();

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (toolbar != null) {
            outState.putString(STATE_TOOLBAR_TITLE, toolbar.getTitle().toString());
        }

        super.onSaveInstanceState(outState);
        outState.clear();
    }

    protected void attachFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    protected void toggleNavigationDrawer() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        } else {
            drawerLayout.openDrawer(navigationView);
        }
    }

    private void logOut() {
        // start service in order to remove data
        Intent removeDataIntent = new Intent(MenuActivity.this, WorkService.class);
        removeDataIntent.putExtra(WorkService.METHOD, WorkService.METHOD_REMOVE_ALL_DATA);
        startService(removeDataIntent);

        // start LoginActivity
        Intent startLoginActivity = new Intent(MenuActivity.this, LoginActivity.class);
        startActivity(startLoginActivity);
        overridePendingTransition(R.anim.activity_close_enter, R.anim.activity_close_exit);
        finish();
    }
}
