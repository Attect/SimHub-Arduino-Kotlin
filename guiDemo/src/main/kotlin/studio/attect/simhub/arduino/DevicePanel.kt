package studio.attect.simhub.arduino

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope


@Composable
fun DevicePanel(appScope: CoroutineScope) {
    Row(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {

    }
}

@Composable
fun DeviceAbilityList() {

}

@Composable
fun DeviceAbilityListItem() {

}

enum class DeviceAbility {
    /**
     * 设备信息
     */
    DEVICE_INFO
}