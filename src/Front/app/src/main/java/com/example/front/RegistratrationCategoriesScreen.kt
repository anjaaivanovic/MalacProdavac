 package com.example.front

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Preview
fun RegistrationCategories() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item{
            Title()
            Spacer(modifier = Modifier.height(10.dp))
        }
        item{
            //FlowRow {
                Cards()
                Box(
                    contentAlignment = Alignment.Center
                )
                {
                    Spacer(modifier = Modifier.height(20.dp))
                    MediumBlueButton(text = "Continue", onClick = { /*TODO*/ }, width = 0.90f)
                }

            //}
        }
    }
}

@Composable
fun Title() {
    Box(
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            painter = painterResource(id = R.drawable.ellipsebrojdva),
            contentDescription = "Elipse",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Choose at least 2 categories:",
            fontSize = 25.sp,
            //style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(top = 50.dp, start = 20.dp, end = 20.dp, bottom = 15.dp),
            lineHeight = 37.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.lexend)),
            color = Color.White
        )
    }
}

data class CardData(val image: Int, val description: String)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Cards() {
    val cardData = listOf(
        CardData(R.drawable.foodicon, "Food"),
        CardData(R.drawable.drinkicon, "Drink"),
        CardData(R.drawable.toolsicon, "Tools"),
        CardData(R.drawable.clothesicon, "Clothes"),
        CardData(R.drawable.necklaceicon, "Jewerly"),
        CardData(R.drawable.footwearicon, "Footwear"),
        CardData(R.drawable.furnitureicon, "Furniture"),
        CardData(R.drawable.potteryicon, "Pottery"),
        CardData(R.drawable.cosmeticsicon, "Beauty"),
        CardData(R.drawable.articon, "Decorations"),
        CardData(R.drawable.healthicon, "Health"),
        CardData(R.drawable.dotsicon, "Other"),
    )

    LazyVerticalGrid (columns = GridCells.Fixed(3),
    modifier = Modifier.padding(start=20.dp,end=20.dp, top=20.dp, bottom = 25.dp)
        .heightIn(400.dp,600.dp)
    ) {
        items(cardData) { card ->
            ClickableCard(
                image = card.image,
                description = card.description,
                onClick = {

                }
            )
        }
    }

}

@Composable
fun ClickableCard(
    image: Int,
    description: String,
    onClick: () -> Unit
) {
    var isCardClicked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(20),
            modifier = Modifier
                .padding(2.dp)
                .clickable {
                    isCardClicked = !isCardClicked
                }
                .height(80.dp)
                .width(80.dp)
                //
        )
        {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
                    .background(if (isCardClicked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary)
                ,
            )
            {
                Image(
                    painter = painterResource(id = image),
                    contentDescription = "Food Icon",
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(0.dp))

        Text(
            text = description,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
    }

}

