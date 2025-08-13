package br.com.marllonbruno.fitnesstracker.android.ui.viewmodel

import br.com.marllonbruno.fitnesstracker.android.MainDispatcherRule
import br.com.marllonbruno.fitnesstracker.android.data.remote.ProfileDataResponse
import br.com.marllonbruno.fitnesstracker.android.data.repository.ProfileRepository
import br.com.marllonbruno.fitnesstracker.android.model.ActivityLevel
import br.com.marllonbruno.fitnesstracker.android.model.Gender
import br.com.marllonbruno.fitnesstracker.android.model.Objective
import io.mockk.mockk
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class ProfileSetupViewModelTest {

    // Aplicação da regra para controlar as coroutines
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Cria o mock do repositorio
    private val profileRepository: ProfileRepository = mockk()

    // Cria a instância do ViewModel que será testado
    private lateinit var viewModel: ProfileSetupViewModel

    @Test
    fun `onEvent GenderSelected - deve atualizar o estado da UI com o gênero selecionado`() =
        runTest{

            // Arrange (Arranjo)
            viewModel = ProfileSetupViewModel(profileRepository)
            val selectedGender = Gender.MALE

            // Act (Ação)
            viewModel.onEvent(ProfileSetupEvent.GenderSelected(selectedGender))

            // Assert (Verificação)
            val currentState = viewModel.uiState.value
            assertThat(currentState.gender).isEqualTo(selectedGender)

        }

    @Test
    fun `submitData - quando o repositório retorna sucesso - deve atualizar o estado para sucesso`() = runTest {
        // Arrange (Arranjo)
        val successResponse = mockk<ProfileDataResponse>() // Cria um objeto de resposta fake
        coEvery { profileRepository.updateProfile(any()) } returns successResponse
        viewModel = ProfileSetupViewModel(profileRepository)

        // Pré-popula o estado diretamente, se necessário, ou através de eventos
        viewModel.onEvent(ProfileSetupEvent.HeightChanged("180"))
        viewModel.onEvent(ProfileSetupEvent.WeightChanged("80.5"))
        viewModel.onEvent(ProfileSetupEvent.BirthDateChanged(LocalDate.of(1990, 1, 1)))
        viewModel.onEvent(ProfileSetupEvent.GenderSelected(Gender.MALE))
        viewModel.onEvent(ProfileSetupEvent.ActivityLevelSelected(ActivityLevel.ACTIVE))
        viewModel.onEvent(ProfileSetupEvent.ObjectiveSelected(Objective.MAINTAIN_WEIGHT))

        // Act (Ação)
        viewModel.onEvent(ProfileSetupEvent.SaveProfile)

        // Assert (Verificação)
        // Em vez de usar Turbine, verificamos o estado final do ViewModel diretamente.
        // O runTest garante que todas as coroutines pendentes (como a do viewModelScope) terminem
        // antes de prosseguirmos para esta linha.
        val finalState = viewModel.uiState.value
        assertThat(finalState.isLoading).isFalse()
        assertThat(finalState.updatedProfile).isEqualTo(successResponse)
        assertThat(finalState.errorMessage).isNull()
    }

    @Test
    fun `submitData - quando o repositório retorna falha - deve atualizar o estado com erro`() = runTest {
        // Arrange
        coEvery { profileRepository.updateProfile(any()) } returns null
        viewModel = ProfileSetupViewModel(profileRepository)

        // Pré-popula o estado
        viewModel.onEvent(ProfileSetupEvent.HeightChanged("180"))
        viewModel.onEvent(ProfileSetupEvent.WeightChanged("80.5"))
        viewModel.onEvent(ProfileSetupEvent.BirthDateChanged(LocalDate.of(1990, 1, 1)))
        viewModel.onEvent(ProfileSetupEvent.GenderSelected(Gender.MALE))
        viewModel.onEvent(ProfileSetupEvent.ActivityLevelSelected(ActivityLevel.ACTIVE))
        viewModel.onEvent(ProfileSetupEvent.ObjectiveSelected(Objective.MAINTAIN_WEIGHT))

        // Act
        viewModel.onEvent(ProfileSetupEvent.SaveProfile)

        // Assert
        // Verificamos o estado final diretamente
        val finalState = viewModel.uiState.value
        assertThat(finalState.isLoading).isFalse()
        assertThat(finalState.updatedProfile).isNull()
        assertThat(finalState.errorMessage).isNotNull()
    }
}