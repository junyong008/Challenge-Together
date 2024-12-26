package com.yjy.data.user.impl.mapper

import com.yjy.data.network.response.user.GetAccountTypeResponse
import com.yjy.model.common.AccountType

internal fun GetAccountTypeResponse.toModel() = AccountType.valueOf(this.type)
