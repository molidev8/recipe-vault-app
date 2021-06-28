package com.moliverac8.recipevault.ui.endToEnd


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.moliverac8.recipevault.R
import com.moliverac8.recipevault.ui.MainActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class NavigateToAccountAndBack {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun navigateToAccountAndBack() {
        val bottomNavigationItemView = onView(
            allOf(
                withId(R.id.account), withContentDescription("My Account"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.bottomNav),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        bottomNavigationItemView.perform(click())

        var imageButton = onView(
            allOf(
                withId(R.id.newRecipeBtn), withContentDescription("Add recipe"),
                withParent(withParent(withId(android.R.id.content))),
            )
        )
        imageButton.check(matches(not(isDisplayed())))

        val imageView = onView(
            allOf(
                withId(R.id.logo),
                withParent(withParent(withId(R.id.fragmentMaster))),
                isDisplayed()
            )
        )
        imageView.check(matches(isDisplayed()))

        val textView = onView(
            allOf(
                withId(R.id.logo_title), withText("Recipe Vault"),
                withParent(withParent(withId(R.id.fragmentMaster))),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Recipe Vault")))

        val textView2 = onView(
            allOf(
                withId(R.id.sign_in_help_text),
                withText("Sign in to make backups of your recipes in Dropbox"),
                withParent(withParent(withId(R.id.fragmentMaster))),
                isDisplayed()
            )
        )
        textView2.check(matches(withText("Sign in to make backups of your recipes in Dropbox")))

        val button = onView(
            allOf(
                withId(R.id.loginBtn), withText("LOGIN TO DROPBOX"),
                withParent(withParent(withId(R.id.fragmentMaster))),
                isDisplayed()
            )
        )
        button.check(matches(isDisplayed()))

        val button2 = onView(
            allOf(
                withId(R.id.loginBtn), withText("LOGIN TO DROPBOX"),
                withParent(withParent(withId(R.id.fragmentMaster))),
                isDisplayed()
            )
        )
        button2.check(matches(isDisplayed()))

        val bottomNavigationItemView2 = onView(
            allOf(
                withId(R.id.my_recipes), withContentDescription("My Recipes"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.bottomNav),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        bottomNavigationItemView2.perform(click())

        imageButton = onView(
            allOf(
                withId(R.id.newRecipeBtn), withContentDescription("Add recipe"),
                withParent(withParent(withId(android.R.id.content))),
                isDisplayed()
            )
        )
        imageButton.check(matches(isDisplayed()))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
