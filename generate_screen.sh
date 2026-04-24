#!/bin/bash

# ==========================================
# PaceApp Feature Generator (Modular KMM-Style)
# ==========================================

# 1. Configuration (Set these once per project!)
APP_PACKAGE_NAME="com.example.paceapp"
APP_FOLDER_PATH="app/src/main/java/com/example/paceapp"

# 2. Core Library Namespaces (These NEVER change across projects!)
CORE_UI_PACKAGE="com.wvelabs.core_ui"
CORE_NETWORK_PACKAGE="com.wvelabs.core_network"

# 3. Validate Input
if [ -z "$1" ]; then
    echo "❌ Error: Missing arguments."
    echo "💡 Usage 1 (Single Screen): bash generate_screen.sh splash"
    echo "💡 Usage 2 (Nested Screen): bash generate_screen.sh auth login"
    exit 1
fi

# =====================================================================
# Pre-Flight: Check & Create BaseViewModel
# =====================================================================
BASE_VM_PATH="${APP_FOLDER_PATH}/core/base"
BASE_VM_FILE="${BASE_VM_PATH}/BaseViewModel.kt"

echo "🔍 Checking Architecture Prerequisites..."

if [ ! -f "$BASE_VM_FILE" ]; then
    echo "⚠️ BaseViewModel not found. Generating it now..."
    mkdir -p "$BASE_VM_PATH"

cat <<EOF > "$BASE_VM_FILE"
package ${APP_PACKAGE_NAME}.core.base

import ${CORE_UI_PACKAGE}.base.CoreViewModel
import ${CORE_UI_PACKAGE}.base.ViewEvent
import ${CORE_UI_PACKAGE}.base.ViewSideEffect
import ${CORE_UI_PACKAGE}.base.ViewState

abstract class BaseViewModel<S : ViewState, E : ViewEvent, Ef : ViewSideEffect> :
    CoreViewModel<S, E, Ef>() {

    // Clean syntax helper for child ViewModels
    protected val currentState: S get() = state.value
}
EOF
    echo "✅ BaseViewModel created successfully!"
else
    # Scan existing file for currentState
    if ! grep -q "currentState" "$BASE_VM_FILE"; then
        echo "⚠️ WARNING: BaseViewModel exists but is missing the 'currentState' helper."
        echo "   Please add this manually to your BaseViewModel.kt:"
        echo "   protected val currentState: S get() = state.value"
    else
        echo "✅ BaseViewModel verified!"
    fi
fi

# =====================================================================
# 4. Handle 1 vs 2 Arguments (Feature Generation)
# =====================================================================
FEATURE=$(echo "$1" | tr '[:upper:]' '[:lower:]')

if [ -z "$2" ]; then
    # ONE ARGUMENT MODE
    SCREEN=$FEATURE
    SCREEN_PASCAL="$(tr '[:lower:]' '[:upper:]' <<< ${1:0:1})${1:1}"
    PACKAGE_PATH="${APP_PACKAGE_NAME}.features.${FEATURE}"
    BASE_FILE_PATH="${APP_FOLDER_PATH}/features/${FEATURE}"
else
    # TWO ARGUMENTS MODE
    SCREEN=$(echo "$2" | tr '[:upper:]' '[:lower:]')
    SCREEN_PASCAL="$(tr '[:lower:]' '[:upper:]' <<< ${2:0:1})${2:1}"
    PACKAGE_PATH="${APP_PACKAGE_NAME}.features.${FEATURE}.${SCREEN}"
    BASE_FILE_PATH="${APP_FOLDER_PATH}/features/${FEATURE}/${SCREEN}"
fi

echo "🚀 Generating Feature: $SCREEN_PASCAL..."

# =====================================================================
# 5. Create Contract File
# =====================================================================
CONTRACT_FILE_NAME="${SCREEN_PASCAL}Contract.kt"
CONTRACT_FILE_CONTENT="package ${PACKAGE_PATH}

import ${CORE_UI_PACKAGE}.base.ViewEvent
import ${CORE_UI_PACKAGE}.base.ViewSideEffect
import ${CORE_UI_PACKAGE}.base.ViewState

class ${SCREEN_PASCAL}Contract {
    data class State(
        val isLoading: Boolean = false
    ) : ViewState

    sealed class Event : ViewEvent {
        data object Init : Event()
    }

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
    }
}"

mkdir -p "$BASE_FILE_PATH"
echo "$CONTRACT_FILE_CONTENT" > "${BASE_FILE_PATH}/${CONTRACT_FILE_NAME}"

# =====================================================================
# 6. Create ViewModel File
# =====================================================================
VIEWMODEL_FILE_NAME="${SCREEN_PASCAL}ViewModel.kt"
VIEWMODEL_FILE_CONTENT="package ${PACKAGE_PATH}

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import ${APP_PACKAGE_NAME}.core.base.BaseViewModel

import ${PACKAGE_PATH}.${SCREEN_PASCAL}Contract.Effect
import ${PACKAGE_PATH}.${SCREEN_PASCAL}Contract.Event
import ${PACKAGE_PATH}.${SCREEN_PASCAL}Contract.State

@HiltViewModel
class ${SCREEN_PASCAL}ViewModel @Inject constructor() : BaseViewModel<State, Event, Effect>() {

    override fun setInitialState() = State()

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Init -> {
                // TODO: Initialize Data
            }
        }
    }
}"

echo "$VIEWMODEL_FILE_CONTENT" > "${BASE_FILE_PATH}/${VIEWMODEL_FILE_NAME}"

# =====================================================================
# 7. Create Screen File (State Wrapper)
# =====================================================================
SCREEN_FILE_NAME="${SCREEN_PASCAL}Screen.kt"
SCREEN_FILE_CONTENT="package ${PACKAGE_PATH}

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest

import ${PACKAGE_PATH}.${SCREEN_PASCAL}Contract.Effect
import ${PACKAGE_PATH}.${SCREEN_PASCAL}Contract.Event
import ${PACKAGE_PATH}.components.${SCREEN_PASCAL}Content

@Composable
fun ${SCREEN_PASCAL}Screen(
    viewModel: ${SCREEN_PASCAL}ViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is Effect.NavigateBack -> onBack()
            }
        }
    }

    ${SCREEN_PASCAL}Content(
        state = state,
        onEvent = viewModel::setEvent
    )
}"

echo "$SCREEN_FILE_CONTENT" > "${BASE_FILE_PATH}/${SCREEN_FILE_NAME}"

# =====================================================================
# 8. Create Content File (Stateless UI)
# =====================================================================
CONTENT_FILE_PATH="${BASE_FILE_PATH}/components"
CONTENT_FILE_NAME="${SCREEN_PASCAL}Content.kt"
CONTENT_FILE_CONTENT="package ${PACKAGE_PATH}.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import ${PACKAGE_PATH}.${SCREEN_PASCAL}Contract.State
import ${PACKAGE_PATH}.${SCREEN_PASCAL}Contract.Event

@Composable
internal fun ${SCREEN_PASCAL}Content(
    state: State,
    onEvent: (Event) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = \"${SCREEN_PASCAL} Screen\")
    }
}"

mkdir -p "$CONTENT_FILE_PATH"
echo "$CONTENT_FILE_CONTENT" > "${CONTENT_FILE_PATH}/${CONTENT_FILE_NAME}"

# =====================================================================
# 9. Create Navigation Route File
# =====================================================================
NAV_FILE_PATH="${BASE_FILE_PATH}/navigation"
NAV_FILE_NAME="${SCREEN_PASCAL}Navigation.kt"
NAV_FILE_CONTENT="package ${PACKAGE_PATH}.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import ${PACKAGE_PATH}.${SCREEN_PASCAL}Screen

@Serializable
data object ${SCREEN_PASCAL}Route

fun NavGraphBuilder.${SCREEN,,}Screen(
    onBack: () -> Unit
) {
    composable<${SCREEN_PASCAL}Route> {
        ${SCREEN_PASCAL}Screen(
            onBack = onBack
        )
    }
}"

mkdir -p "$NAV_FILE_PATH"
echo "$NAV_FILE_CONTENT" > "${NAV_FILE_PATH}/${NAV_FILE_NAME}"

echo "🎉 Feature '${SCREEN_PASCAL}' generated successfully!"