package com.github.wanderwise_inc.app.ui.search

/*
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

// chips
data class ChipsModel(
    val name: String,
    val subList: List<String>? = null,
    val textExpanded: String? = null,
    val leadingIcon: ImageVector? = null,
    val trailingIcon: ImageVector? = null,
    val selectedIcon: ImageVector? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun FilterChip() {
  val filterList =
      listOf(
          ChipsModel(
              name = "Test", leadingIcon = Icons.Default.Home, selectedIcon = Icons.Default.Home),
          ChipsModel(
              name = "Price Range",
              subList = listOf("0-20$", "20-40$", "40-+$"),
              leadingIcon = Icons.Default.Check,
              selectedIcon = Icons.Default.Place,
          ),
          ChipsModel(name = "Test2", selectedIcon = Icons.Default.Check),
          ChipsModel(name = "Test3", trailingIcon = Icons.Default.Close))
  val selectedItems = mutableStateListOf<String>()
  var isSelected by remember { mutableStateOf(false) }

  // val selectedItems = mutableStateListOf<String>()
  // var isSelected by remember { mutableStateOf(false) }

  LazyRow {
    items(filterList) { item ->
      isSelected = selectedItems.contains(item.name)
      Spacer(modifier = Modifier.padding(5.dp))
      if (item.subList != null) {
        ChipWithSubItems(chipLabel = item.name, chipItems = item.subList)
      } else {
        FilterChip(
            selected = isSelected,
            onClick = {
              when (selectedItems.contains(item.name)) {
                true -> selectedItems.remove(item.name)
                false -> selectedItems.add(item.name)
              }
              Log.d("Clicked", "BUTTON WAS CLICKED")
            },
            label = { Text(text = item.name) },
            leadingIcon = {
              if (isSelected && item.selectedIcon != null) {
                Icon(item.selectedIcon, contentDescription = item.name)
              } else if (item.leadingIcon != null) {
                Icon(item.leadingIcon, contentDescription = item.name)
              }
              Log.d("Clicked", "ENTERED ICON")
            },
            trailingIcon = {
              if (item.trailingIcon != null && isSelected)
                  Icon(item.trailingIcon, contentDescription = item.name)
              Log.d("Clicked", "ENTERED TRAILING ICON")
            })
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChipWithSubItems(chipLabel: String, chipItems: List<String>) {
  var isSelected by remember { mutableStateOf(false) }
  var showSubList by remember { mutableStateOf(false) }
  var filterName by remember { mutableStateOf(chipLabel) }

  ExposedDropdownMenuBox(
      expanded = showSubList, onExpandedChange = { showSubList = !showSubList }) {
        FilterChip(
            selected = isSelected,
            onClick = {
              isSelected = true
              // showSubList = ! showSubList
            },
            label = { Text(text = filterName.ifEmpty { chipLabel }) },
            trailingIcon = {
              Icon(
                  modifier = Modifier.rotate(if (showSubList) 180f else 0f),
                  imageVector = Icons.Default.ArrowDropDown,
                  contentDescription = "List")
            })
        ExposedDropdownMenu(
            expanded = showSubList,
            onDismissRequest = { showSubList = false },
        ) {
          chipItems.forEach { subListItem ->
            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                  filterName = subListItem
                  showSubList = false
                },
                colors =
                    ButtonDefaults.textButtonColors(
                        containerColor =
                            if (subListItem == filterName || subListItem == chipLabel) {
                              MaterialTheme.colorScheme.primaryContainer
                            } else {
                              Color.Transparent
                            })) {
                  Text(text = subListItem, color = Color.Black)
                }
          }
        }
      }
}
*/
