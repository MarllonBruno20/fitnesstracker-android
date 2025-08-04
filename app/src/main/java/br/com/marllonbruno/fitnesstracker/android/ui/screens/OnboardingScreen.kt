package br.com.marllonbruno.fitnesstracker.android.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.marllonbruno.fitnesstracker.android.R

// Esta é a nossa tela principal de Onboarding
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onOnboardingFinished: () -> Unit) {
    // Lista de páginas que queremos mostrar
    val pages = listOf(
        OnboardingPage(stringResource(R.string.onboarding_welcome_title), stringResource(R.string.onboarding_welcome_subtitle), R.drawable.ic_onboarding_fitness_tracker),
        OnboardingPage( stringResource(R.string.onboarding_recipes_title), stringResource(R.string.onboarding_recipes_subtitle), R.drawable.ic_onboarding_recipes),
        OnboardingPage( stringResource(R.string.onboarding_progress_title), stringResource(R.string.onboarding_progress_subtitle), R.drawable.ic_onboarding_stats)
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { pageIndex ->
                OnboardingPageContent(page = pages[pageIndex])
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Botão "Voltar" (só aparece a partir da segunda página)
                if (pagerState.currentPage > 0) {
                    IconButton(onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back))
                    }
                } else {
                    // Espaço vazio para manter o botão "Próximo" na direita
                    Spacer(modifier = Modifier.width(48.dp))
                }

                // Indicador de página (as bolinhas) - Opcional mas recomendado
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pages.size) { index ->
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(if (index == pagerState.currentPage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                        )
                    }
                }

                // Botão "Próximo" ou "Começar"
                if (pagerState.currentPage < pages.size - 1) {
                    IconButton(onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = stringResource(R.string.next))
                    }
                } else {
                    // Na última página, o botão "Começar" substitui o "Próximo"
                    TextButton(onClick = onOnboardingFinished) {
                        Text(stringResource(R.string.get_started))
                    }
                }
            }
        }
    }
}

// Um data class para representar os dados de cada página
data class OnboardingPage(val title: String,
                          val description: String,
                          @DrawableRes val imageRes: Int)

// O Composable que desenha o conteúdo de uma única página
@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = page.title,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .aspectRatio(1f)
        )

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}