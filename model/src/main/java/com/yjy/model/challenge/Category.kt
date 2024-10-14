package com.yjy.model.challenge

import com.yjy.model.R

enum class Category(val displayNameResId: Int) {
    ALL(R.string.model_category_all),
    QUIT_SMOKING(R.string.model_category_quit_smoking),
    QUIT_DRINKING(R.string.model_category_quit_drinking),
    QUIT_PORN(R.string.model_category_quit_porn),
    QUIT_GAMING(R.string.model_category_quit_gaming),
    QUIT_YOUTUBE(R.string.model_category_quit_youtube),
    QUIT_SNS(R.string.model_category_quit_sns),
    QUIT_GLUTEN(R.string.model_category_quit_gluten),
    QUIT_GAMBLING(R.string.model_category_quit_gambling),
    QUIT_DRUGS(R.string.model_category_quit_drugs),
    QUIT_COFFEE(R.string.model_category_quit_coffee),
    QUIT_EATING_OUT(R.string.model_category_quit_eating_out),
    QUIT_SHOPPING(R.string.model_category_quit_shopping),
    QUIT_SWEARING(R.string.model_category_quit_swearing),
    QUIT_SMARTPHONE(R.string.model_category_quit_smartphone);
}
