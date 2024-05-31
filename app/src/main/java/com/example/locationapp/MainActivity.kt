package com.example.locationapp

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.example.locationapp.ui.theme.LocationAppTheme
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel :LocationViewModel = viewModel()
            LocationAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun MyApp( viewModel: LocationViewModel) {
    val context = LocalContext.current
    val locationUtils = LocationUtils(context)

    LocationDisplay(locationUtils = locationUtils,viewModel, context = context)
}


@Composable
fun LocationDisplay(
    locationUtils: LocationUtils,
    viewModel: LocationViewModel,
    context: Context
) {

    val location = viewModel.location.value

    // 결과를 위해 액티비티를 시작하라는 요청을 등록하는 것
    val requestPermissonLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(), // 여러 권한을 요청할 수 있도록 함
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true //
            ) {
                // 위치 권한이 승인됐다면 위치를 업데이트할 것
                locationUtils.requestLocationUpdates(viewModel = viewModel)

            } else {
                // 위치 권한이 거부됐다면 사용자에게 알림을 보여줄 것
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )

                if (rationaleRequired) {
                    // 사용자에게 권한을 요청하는 이유를 설명해야 함
                    Toast.makeText(context, "위치 권한이 필요합니다.", Toast.LENGTH_LONG).show()
                } else {
                    // 사용자에게 설정으로 이동하라는 메시지를 보여줄 것
                    Toast.makeText(context, "안드로이드 설정에서 활성화 해주세요", Toast.LENGTH_LONG).show()
                }
            }
        }
    )




    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (location != null) {
            Text(text = "위도: ${location.latitude}")
            Text(text = "경도: ${location.longitude}")
        } else{
            Text(text = "위치를 확인할 수 없습니다.")
        }

        Button(onClick = {
            if (locationUtils.hasLocationPermission(context)) {
                // 만약 권한이 승인됐다면 위치를 업데이트할 것
                locationUtils.requestLocationUpdates(viewModel = viewModel)
            } else {
                // 위치 권한을 요청할 것
                requestPermissonLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }) {
            Text(text = "위치 권한 요청")
        }

    }
}