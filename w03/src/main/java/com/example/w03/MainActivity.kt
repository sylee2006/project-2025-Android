package com.example.w03

import android.R.attr.contentDescription
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.w03.ui.theme.SyleeTheme
import org.w3c.dom.Text

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SyleeTheme {
                HomeScreen()
            }
        }
    }
}
@Composable
fun HomeScreen() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "세계 1위 판매 음료",
            style = MaterialTheme.typography.headlineMedium
        )

        Image(
            painter = painterResource(id = R.drawable.img_1),
            contentDescription = "코카콜라",
            modifier = Modifier
                .size(300.dp)
                .padding(16.dp)
        )

        Text(
            "코카 콜라",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.coupang.com/vp/products/9371774?itemId=21184770164&vendorItemId=88377301422&pickType=COU_PICK&q=%EC%BD%94%EC%B9%B4%EC%BD%9C%EB%9D%BC&searchId=ecca8a3b4783271&sourceType=search&itemsCount=35&searchRank=1&rank=1&traceId=mfni7j90"))
            context.startActivity(intent)
        }) {
            Text("주문하기")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}

