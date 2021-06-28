package com.moliverac8.recipevault.ui.endToEnd


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.moliverac8.recipevault.R
import com.moliverac8.recipevault.ui.MainActivity
import com.moliverac8.recipevault.ui.recipeList.RecipeListAdapter
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CreateRecipe {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun createRecipeAndRemove() {
        val newRecipeBtn = onView(
            allOf(
                withId(R.id.newRecipeBtn), withContentDescription("Add recipe"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content), 0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        newRecipeBtn.perform(click())

        newRecipeBtn.check(doesNotExist())

        val saveBtn = onView(
            allOf(
                withId(R.id.save_recipe), withContentDescription("Save"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.top_bar),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )

        newRecipeBtn.check(doesNotExist())

        val textInputEditText = onView(
            allOf(
                withId(R.id.set_title_edit)
            )
        )

        textInputEditText.perform(scrollTo(), replaceText("Testing"), closeSoftKeyboard())

        textInputEditText.perform(pressImeActionButton())

        val textInputEditText3 = onView(
            allOf(
                withId(R.id.set_time_to_cook_edit)
            )
        )

        textInputEditText3.perform(scrollTo(), replaceText("25"), closeSoftKeyboard())

        textInputEditText3.perform(pressImeActionButton())

        saveBtn.perform(click())

        val textView = onView(
            allOf(
                withId(R.id.recipeTitle), withText("Testing"),
                withParent(withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Testing")))

        val textView2 = onView(
            allOf(
                withId(R.id.timeToCook), withText("25 minutes"),
                withParent(withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))),
                isDisplayed()
            )
        )
        textView2.check(matches(withText("25 minutes")))

        onView(withId(R.id.recipeList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecipeListAdapter.ViewHolder>(
                0,
                swipeRight()
            )
        )
    }

    @Test
    fun createRecipeWithEmptyFields() {
        val newRecipeBtn = onView(
            allOf(
                withId(R.id.newRecipeBtn), withContentDescription("Add recipe"),
                childAtPosition(
                    childAtPosition(
                        withId(android.R.id.content), 0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        newRecipeBtn.perform(click())

        newRecipeBtn.check(doesNotExist())

        val saveBtn = onView(
            allOf(
                withId(R.id.save_recipe), withContentDescription("Save"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.top_bar),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )

        saveBtn.perform(click())

        newRecipeBtn.check(doesNotExist())

        val textInputEditText = onView(
            allOf(
                withId(R.id.set_title_edit)
            )
        )

        textInputEditText.perform(scrollTo(), replaceText("Testing"), closeSoftKeyboard())

        textInputEditText.perform(pressImeActionButton())

        val textInputEditText3 = onView(
            allOf(
                withId(R.id.set_time_to_cook_edit)
            )
        )

        textInputEditText3.perform(scrollTo(), replaceText("25"), closeSoftKeyboard())

        textInputEditText3.perform(pressImeActionButton())

        saveBtn.perform(click())

        val textView = onView(
            allOf(
                withId(R.id.recipeTitle), withText("Testing"),
                withParent(withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Testing")))

        val textView2 = onView(
            allOf(
                withId(R.id.timeToCook), withText("25 minutes"),
                withParent(withParent(IsInstanceOf.instanceOf(android.view.ViewGroup::class.java))),
                isDisplayed()
            )
        )
        textView2.check(matches(withText("25 minutes")))
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
