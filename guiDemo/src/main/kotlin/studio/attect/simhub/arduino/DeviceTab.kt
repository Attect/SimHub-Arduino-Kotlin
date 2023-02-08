package studio.attect.simhub.arduino

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope

@Composable
fun DeviceTab(appScope: CoroutineScope) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f)) {
            val tabScrollState = rememberLazyListState()
            LazyRow(modifier = Modifier, state = tabScrollState) {
                items(DeviceManager.allComDevice) {
                    DeviceTab(it)
                }
            }
            HorizontalScrollbar(rememberScrollbarAdapter(tabScrollState))
        }

        Box(modifier = Modifier.clickable { refreshDevice(appScope) }.padding(8.dp)) {
            Text("刷新", fontSize = 14.sp)
        }
    }
}


@Composable
private fun DeviceTab(simHubDevice: SimHubDevice) {
    var tabColor = Color.LightGray
    var bottomColor = Color.Transparent

    if (currentDevice == simHubDevice) {
        tabColor = Color.White
        bottomColor = Color.Blue
    }

    Column(
        modifier = Modifier.width(IntrinsicSize.Max).height(34.dp).background(tabColor).clickable {
            currentDevice = simHubDevice
            println("click ${simHubDevice.serialPort}")
        }.padding(start = 8.dp, top = 8.dp, end = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(text = "设备:" + simHubDevice.serialPort.descriptivePortName, fontSize = 14.sp)
        }
        Box(modifier = Modifier.fillMaxWidth().padding(top = 2.dp, bottom = 2.dp).height(2.dp).background(bottomColor))
    }

}