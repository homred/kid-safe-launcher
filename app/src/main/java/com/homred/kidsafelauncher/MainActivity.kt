package com.homred.kidsafelauncher

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StatFs
import android.provider.Settings
import android.text.format.DateFormat
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.homred.kidsafelauncher.data.AppRepository
import com.homred.kidsafelauncher.data.SettingsStore
import com.homred.kidsafelauncher.logic.AppFilter
import com.homred.kidsafelauncher.logic.PinRules
import com.homred.kidsafelauncher.logic.RestrictionPolicy
import com.homred.kidsafelauncher.model.AppEntry
import com.homred.kidsafelauncher.ui.theme.KidSafeLauncherTheme
import com.homred.kidsafelauncher.util.safeStartActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settingsStore = SettingsStore(this)
        val appRepository = AppRepository(this)
        setContent {
            KidSafeLauncherTheme {
                LauncherScreen(settingsStore, appRepository)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun LauncherScreen(settingsStore: SettingsStore, appRepository: AppRepository) {
    val context = LocalContext.current
    val apps by produceState(initialValue = emptyList<AppEntry>()) {
        value = appRepository.getLaunchableApps()
    }
    var search by rememberSaveable { mutableStateOf("") }
    var selectedApp by remember { mutableStateOf<AppEntry?>(null) }
    var showDeviceInfo by remember { mutableStateOf(false) }
    var showParentDialog by remember { mutableStateOf(false) }
    var showPinSetup by remember { mutableStateOf(false) }
    var showPinVerify by remember { mutableStateOf(false) }
    var parentMode by remember { mutableStateOf(false) }
    var showRestrictionDialog by remember { mutableStateOf(false) }
    var showGuide by remember { mutableStateOf(settingsStore.shouldShowGuide()) }
    var showQuickPanel by remember { mutableStateOf(false) }
    var whitelist by remember { mutableStateOf(settingsStore.getWhitelist()) }

    val filteredApps = remember(apps, search) { AppFilter.filterByName(apps, search) }

    if (showGuide) {
        AlertDialog(
            onDismissRequest = {
                showGuide = false
                settingsStore.markGuideShown()
            },
            title = { Text("欢迎使用儿童健康桌面") },
            text = { Text("建议先将本应用设为默认桌面，并在家长模式中设置 PIN 与白名单应用。") },
            confirmButton = {
                TextButton(onClick = {
                    showGuide = false
                    settingsStore.markGuideShown()
                }) {
                    Text("我知道了")
                }
            }
        )
    }

    if (showPinSetup) {
        PinDialog(
            title = "设置家长 PIN",
            onDismiss = { showPinSetup = false },
            onConfirm = { pin ->
                if (PinRules.isValid(pin)) {
                    settingsStore.savePin(pin)
                    showPinSetup = false
                    parentMode = true
                } else {
                    Toast.makeText(context, "PIN 必须为 4~6 位数字", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    if (showPinVerify) {
        PinDialog(
            title = "输入家长 PIN",
            onDismiss = { showPinVerify = false },
            onConfirm = { pin ->
                if (PinRules.verify(settingsStore.getPin().orEmpty(), pin)) {
                    parentMode = true
                    showPinVerify = false
                } else {
                    Toast.makeText(context, "PIN 不正确", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    if (showRestrictionDialog) {
        AlertDialog(
            onDismissRequest = { showRestrictionDialog = false },
            title = { Text("应用已受限") },
            text = { Text("该应用不在家长白名单中，暂不可启动。") },
            confirmButton = {
                TextButton(onClick = { showRestrictionDialog = false }) { Text("确定") }
            }
        )
    }

    if (showDeviceInfo) {
        DeviceInfoDialog(onDismiss = { showDeviceInfo = false })
    }

    if (showParentDialog && parentMode) {
        ParentModeDialog(
            apps = apps,
            whitelist = whitelist,
            onUpdate = {
                whitelist = it
                settingsStore.saveWhitelist(it)
            },
            onDismiss = { showParentDialog = false }
        )
    }

    if (showQuickPanel) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showQuickPanel = false },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("快捷入口", style = MaterialTheme.typography.titleMedium)
                QuickSettingChip("Wi‑Fi", Icons.Default.NetworkWifi) {
                    context.safeStartActivity(Intent(Settings.ACTION_WIFI_SETTINGS), "无法打开 Wi‑Fi 设置")
                }
                QuickSettingChip("蓝牙", Icons.Default.Bluetooth) {
                    context.safeStartActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS), "无法打开蓝牙设置")
                }
                QuickSettingChip("显示设置", Icons.Default.Settings) {
                    context.safeStartActivity(Intent(Settings.ACTION_DISPLAY_SETTINGS), "无法打开显示设置")
                }
            }
        }
    }

    val isLargeLayout = remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("儿童健康桌面") },
                actions = {
                    IconButton(onClick = {
                        if (settingsStore.getPin().isNullOrBlank()) {
                            showPinSetup = true
                        } else {
                            showPinVerify = true
                        }
                    }) {
                        Icon(Icons.Default.Lock, contentDescription = "家长模式")
                    }
                    IconButton(onClick = { showQuickPanel = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "快捷面板")
                    }
                }
            )
        }
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->
                        if (dragAmount > 30f) showQuickPanel = true
                    }
                }
        ) {
            val columns = when {
                maxWidth < 260.dp -> 2
                maxWidth < 600.dp -> 3
                else -> 6
            }
            isLargeLayout.value = maxWidth >= 600.dp
            Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                if (isLargeLayout.value) {
                    Row(
                        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(DateFormat.format("HH:mm", System.currentTimeMillis()).toString())
                        Text("已安装应用：${apps.size}")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text("搜索应用") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(onClick = {
                        context.safeStartActivity(Intent(Settings.ACTION_WIFI_SETTINGS), "无法打开 Wi‑Fi 设置")
                    }, label = { Text("Wi‑Fi") }, leadingIcon = { Icon(Icons.Default.NetworkWifi, null) })
                    AssistChip(onClick = {
                        context.safeStartActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS), "无法打开蓝牙设置")
                    }, label = { Text("蓝牙") }, leadingIcon = { Icon(Icons.Default.Bluetooth, null) })
                    AssistChip(onClick = {
                        val opened = context.safeStartActivity(Intent("android.settings.ETHERNET_SETTINGS"), "当前设备不支持网络/以太网设置")
                        if (!opened) {
                            Toast.makeText(context, "当前设备不支持网络/以太网设置", Toast.LENGTH_SHORT).show()
                        }
                    }, label = { Text("网络") }, leadingIcon = { Icon(Icons.Default.Devices, null) })
                    AssistChip(onClick = { showDeviceInfo = true }, label = { Text("设备信息") }, leadingIcon = { Icon(Icons.Default.Settings, null) })
                }

                Spacer(modifier = Modifier.height(8.dp))
                if (selectedApp == null) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(columns),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredApps, key = { it.packageName }) { app ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onClick = {
                                            if (!parentMode && RestrictionPolicy.isRestricted(app.packageName, whitelist)) {
                                                showRestrictionDialog = true
                                            } else {
                                                selectedApp = app
                                            }
                                        },
                                        onLongClick = {
                                            selectedApp = app
                                        }
                                    )
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    app.icon?.let {
                                        Image(
                                            bitmap = it.toBitmap(96, 96).asImageBitmap(),
                                            contentDescription = app.name,
                                            modifier = Modifier.size(48.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(app.name, maxLines = 2)
                                }
                            }
                        }
                    }
                } else {
                    AppDetail(
                        app = selectedApp!!,
                        onBack = { selectedApp = null },
                        onOpen = {
                            val launchIntent = context.packageManager.getLaunchIntentForPackage(selectedApp!!.packageName)
                            if (launchIntent == null) {
                                Toast.makeText(context, "无法打开该应用", Toast.LENGTH_SHORT).show()
                            } else {
                                context.safeStartActivity(launchIntent, "无法打开该应用")
                            }
                        },
                        onUninstall = {
                            if (!selectedApp!!.canUninstall) {
                                Toast.makeText(context, "该应用不可卸载（系统应用或受保护）", Toast.LENGTH_SHORT).show()
                                return@AppDetail
                            }
                            val intent = Intent(Intent.ACTION_DELETE, Uri.parse("package:${selectedApp!!.packageName}"))
                            context.safeStartActivity(intent, "无法发起卸载")
                        }
                    )
                }

                if (parentMode) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showParentDialog = true }) { Text("管理白名单") }
                        TextButton(onClick = { parentMode = false }) { Text("退出家长模式") }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickSettingChip(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null) }
    )
}

@Composable
private fun PinDialog(title: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var pin by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = pin,
                onValueChange = { pin = it.filter { c -> c.isDigit() } },
                label = { Text("4~6 位数字") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(pin) }) { Text("确认") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}

@Composable
private fun ParentModeDialog(
    apps: List<AppEntry>,
    whitelist: Set<String>,
    onUpdate: (Set<String>) -> Unit,
    onDismiss: () -> Unit,
) {
    var editingWhitelist by remember(whitelist) { mutableStateOf(whitelist) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("白名单管理") },
        text = {
            LazyColumn(modifier = Modifier.height(320.dp)) {
                items(apps) { app ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(app.name, modifier = Modifier.weight(1f))
                        Checkbox(
                            checked = app.packageName in editingWhitelist,
                            onCheckedChange = { checked ->
                                editingWhitelist = if (checked) {
                                    editingWhitelist + app.packageName
                                } else {
                                    editingWhitelist - app.packageName
                                }
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onUpdate(editingWhitelist)
                onDismiss()
            }) { Text("保存") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}

@Composable
private fun AppDetail(app: AppEntry, onBack: () -> Unit, onOpen: () -> Unit, onUninstall: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = app.name, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            IconButton(onClick = onBack) {
                Icon(Icons.Default.Close, contentDescription = "返回")
            }
        }
        Button(onClick = onOpen, modifier = Modifier.fillMaxWidth()) {
            Text("打开应用")
        }
        Button(onClick = onUninstall, modifier = Modifier.fillMaxWidth()) {
            Text("卸载应用")
        }
        Text("包名：${app.packageName}")
    }
}

@Composable
private fun DeviceInfoDialog(onDismiss: () -> Unit) {
    val storage = remember {
        val statFs = StatFs(android.os.Environment.getDataDirectory().path)
        val available = statFs.availableBytes / 1024 / 1024
        val total = statFs.totalBytes / 1024 / 1024
        "$available MB / $total MB"
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("设备信息") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("品牌: ${Build.BRAND}")
                Text("型号: ${Build.MODEL}")
                Text("Android: ${Build.VERSION.RELEASE}")
                Text("SDK: ${Build.VERSION.SDK_INT}")
                Text("ABI: ${Build.SUPPORTED_ABIS.joinToString()}")
                Text("可用/总存储: $storage")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("关闭") }
        }
    )
}
