package com.yjy.model.challenge.base

import com.yjy.model.challenge.core.Category
import com.yjy.model.challenge.core.TargetDays

abstract class Challenge {
    abstract val id: Int
    abstract val title: String
    abstract val description: String
    abstract val category: Category
    abstract val targetDays: TargetDays
}
