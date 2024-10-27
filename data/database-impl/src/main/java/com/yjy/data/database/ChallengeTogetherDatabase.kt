package com.yjy.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yjy.data.database.dao.ChallengeDao
import com.yjy.data.database.model.ChallengeEntity
import com.yjy.data.database.util.Converters

@Database(
    entities = [
        ChallengeEntity::class,
    ],
    version = 1
)
@TypeConverters(Converters::class)
internal abstract class ChallengeTogetherDatabase : RoomDatabase() {
    abstract fun challengeDao(): ChallengeDao
}
