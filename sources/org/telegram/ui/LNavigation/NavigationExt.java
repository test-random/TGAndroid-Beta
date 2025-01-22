package org.telegram.ui.LNavigation;

import java.util.ArrayList;
import java.util.List;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.INavigationLayout;

public abstract class NavigationExt {

    public interface FragmentConsumer {
        boolean consume(BaseFragment baseFragment);
    }

    public static boolean backToFragment(BaseFragment baseFragment, FragmentConsumer fragmentConsumer) {
        if (baseFragment != null && baseFragment.getParentLayout() != null) {
            INavigationLayout parentLayout = baseFragment.getParentLayout();
            BaseFragment lastFragment = baseFragment.getParentLayout().getLastFragment();
            List fragmentStack = lastFragment.getParentLayout().getFragmentStack();
            ArrayList arrayList = new ArrayList();
            for (int size = parentLayout.getFragmentStack().size() - 1; size >= 0; size--) {
                if (fragmentConsumer.consume((BaseFragment) fragmentStack.get(size))) {
                    for (int size2 = arrayList.size() - 1; size2 >= 0; size2--) {
                        if (arrayList.get(size2) != lastFragment) {
                            ((BaseFragment) arrayList.get(size2)).removeSelfFromStack();
                        }
                    }
                    lastFragment.lambda$onBackPressed$323();
                    return true;
                }
                arrayList.add((BaseFragment) fragmentStack.get(size));
            }
        }
        return false;
    }
}
