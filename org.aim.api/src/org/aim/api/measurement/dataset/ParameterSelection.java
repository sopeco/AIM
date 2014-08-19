/**
 * Copyright 2014 SAP AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aim.api.measurement.dataset;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.aim.api.measurement.AbstractRecord;

/**
 * A parameter configuration selection.
 * 
 * @author Alexander Wert
 * 
 */
public class ParameterSelection {

	/**
	 * 
	 * @return new instance of parameter selection
	 */
	public static ParameterSelection newSelection() {
		return new ParameterSelection();
	}

	private final Set<Parameter> selection;
	private final Set<Parameter> unEqualSelection;
	private final Set<Parameter> largerSelection;
	private final Set<Parameter> smallerSelection;

	/**
	 * Constructor.
	 */
	public ParameterSelection() {
		selection = new TreeSet<>();
		unEqualSelection = new TreeSet<>();
		largerSelection = new TreeSet<>();
		smallerSelection = new TreeSet<>();
	}

	/**
	 * Tells the selection to select all records which have a smaller value for
	 * the given parameter name then specified by the given parameter.
	 * 
	 * @param parameter
	 *            parameter for which the value should be smaller
	 * @return the modified parameter selection
	 */
	public ParameterSelection smallerOrEquals(Parameter parameter) {
		removeExistingParameterwithSameName(parameter.getName(), smallerSelection);
		smallerSelection.add(parameter);
		return this;
	}

	/**
	 * Tells the selection to select all records which have a smaller value for
	 * the given parameter name then specified by the given parameter.
	 * 
	 * @param parName
	 *            name of the parameter for which the value should be smaller
	 * @param value
	 *            upper limit for that parameter
	 * @return the modified parameter selection
	 */
	public ParameterSelection smallerOrEquals(String parName, Object value) {
		smallerOrEquals(new Parameter(parName, value));
		return this;
	}

	/**
	 * Tells the selection to select all records which have a larger value for
	 * the given parameter name then specified by the given parameter.
	 * 
	 * @param parameter
	 *            parameter for which the value should be larger
	 * @return the modified parameter selection
	 */
	public ParameterSelection largerOrEquals(Parameter parameter) {
		removeExistingParameterwithSameName(parameter.getName(), largerSelection);
		largerSelection.add(parameter);
		return this;
	}

	/**
	 * Tells the selection to select all records which have a larger value for
	 * the given parameter name then specified by the given parameter.
	 * 
	 * @param parName
	 *            name of the parameter for which the value should be larger
	 * @param value
	 *            lower limit for that parameter
	 * @return the modified parameter selection
	 */
	public ParameterSelection largerOrEquals(String parName, Object value) {
		largerOrEquals(new Parameter(parName, value));
		return this;
	}

	/**
	 * Tells the selection to select all records which have a value between
	 * lower and upper limitfor the given parameter name.
	 * 
	 * @param parName
	 *            name of the parameter for which the value should be larger
	 * @param lowerLimit
	 *            lower limit for that parameter
	 * @param upperLimit
	 *            upper limit for that parameter
	 * @return the modified parameter selection
	 */
	public ParameterSelection between(String parName, Object lowerLimit, Object upperLimit) {
		if (!lowerLimit.getClass().equals(upperLimit.getClass())) {
			throw new IllegalArgumentException("Lower and upper limit must have the same type!");
		}
		largerOrEquals(new Parameter(parName, lowerLimit));
		smallerOrEquals(new Parameter(parName, upperLimit));
		return this;
	}

	/**
	 * Selects the given parameter. If a parameter with same name within this
	 * selection exists the old value will be overwritten.
	 * 
	 * @param parameter
	 *            parameter for which the value should be fixed
	 * @return the modified parameter selection
	 */
	public ParameterSelection select(Parameter parameter) {
		removeExistingParameterwithSameName(parameter.getName(), selection);
		selection.add(parameter);
		return this;
	}

	/**
	 * Selects the given parameter. If a parameter with same name within this
	 * selection exists the old value will be overwritten.
	 * 
	 * @param parameter
	 *            parameter for which the value should be excluded
	 * @return the modified parameter selection
	 */
	public ParameterSelection unequal(Parameter parameter) {
		unEqualSelection.add(parameter);
		return this;
	}
	
	/**
	 * Excludes the given parameter from selection.
	 * @param parName parameter name
	 * @param value parameter value
	 * @return selection instance
	 */
	public ParameterSelection unequal(String parName, Object value) {
		unequal(new Parameter(parName, value));
		return this;
	}

	/**
	 * Selects the given parameters.If a parameter with same name within this
	 * selection exists the old value will be overwritten.
	 * 
	 * @param parameters
	 *            parameters for which the value should be fixed
	 * @return the modified parameter selection
	 */
	public ParameterSelection select(Collection<Parameter> parameters) {

		for (Parameter p : parameters) {
			select(p);
		}

		return this;
	}

	/**
	 * Selects the given parameter. If a parameter with same name within this
	 * selection exists the old value will be overwritten.
	 * 
	 * @param parName
	 *            of parameter
	 * @param value
	 *            for the parameter to fix
	 * @return the modified parameter selection
	 */
	public ParameterSelection select(String parName, Object value) {
		return select(new Parameter(parName, value));
	}

	private void removeExistingParameterwithSameName(String parName, Set<Parameter> parameters) {
		Parameter parToRemove = null;
		for (Parameter p : parameters) {
			if (p.getName().equalsIgnoreCase(parName)) {
				parToRemove = p;
			}
		}
		if (parToRemove != null) {
			parameters.remove(parToRemove);
		}
	}

	/**
	 * applies selection to given dataset row.
	 * 
	 * @param dsRow
	 *            dataset row to select from
	 * @return selected dataset row or null if no records could be selected
	 */
	public DatasetRow applyTo(DatasetRow dsRow) {
		return applyToWithSubSelection(dsRow, selection, unEqualSelection, smallerSelection, largerSelection);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private DatasetRow applyToWithSubSelection(DatasetRow dsRow, Set<Parameter> subSelection,
			Set<Parameter> unEqualSelection, Set<Parameter> subSmaller, Set<Parameter> subLarger) {
		DatasetRowBuilder dsRowBuilder = new DatasetRowBuilder(dsRow.getRecordType());

		recordLoop: for (AbstractRecord record : dsRow.getRecords()) {

			if (unEqualSelection != null) {
				for (Parameter parameter : unEqualSelection) {
					if (parameter.getValue().equals(record.getValue(parameter.getName()))) {
						continue recordLoop;
					}
				}
			}

			if (subSelection != null) {
				for (Parameter parameter : subSelection) {
					if (!parameter.getValue().equals(record.getValue(parameter.getName()))) {
						continue recordLoop;
					}
				}
			}

			if (subSmaller != null) {
				for (Parameter parameter : subSmaller) {
					Comparable upperLimit = (Comparable) parameter.getValue();
					Comparable value = (Comparable) record.getValue(parameter.getName());

					if (value.compareTo(upperLimit) > 0) {
						continue recordLoop;
					}
				}
			}

			if (subLarger != null) {
				for (Parameter parameter : subLarger) {
					Comparable lowerLimit = (Comparable) parameter.getValue();
					Comparable value = (Comparable) record.getValue(parameter.getName());

					if (value.compareTo(lowerLimit) < 0) {
						continue recordLoop;
					}
				}
			}

			dsRowBuilder.addRecordWithoutChecks(record);
		}

		for (Parameter parameter : dsRow.getParameters()) {
			dsRowBuilder.addInputParameterWithoutChecks(parameter);
		}

		return dsRowBuilder.build();
	}

	/**
	 * applies selection to given dataset.
	 * 
	 * @param dataset
	 *            dataset to select from
	 * @return selected dataset
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Dataset applyTo(Dataset dataset) {

		Set<Parameter> selectionInputParameters = null;
		Set<Parameter> selectionObservationParameters = null;
		Set<Parameter> unEqualInputParameters = null;
		Set<Parameter> unEqualnObservationParameters = null;
		Set<Parameter> smallerInputParameters = null;
		Set<Parameter> smallerObservationParameters = null;
		Set<Parameter> largerInputParameters = null;
		Set<Parameter> largerObservationParameters = null;

		if (!selection.isEmpty()) {
			selectionInputParameters = new HashSet<>();
			selectionObservationParameters = new HashSet<>();
			splitParametersIntoInputAndOutput(dataset, selectionInputParameters, selectionObservationParameters,
					selection);
		}

		if (!unEqualSelection.isEmpty()) {
			unEqualInputParameters = new HashSet<>();
			unEqualnObservationParameters = new HashSet<>();
			splitParametersIntoInputAndOutput(dataset, unEqualInputParameters, unEqualnObservationParameters,
					unEqualSelection);
		}

		if (!smallerSelection.isEmpty()) {
			smallerInputParameters = new HashSet<>();
			smallerObservationParameters = new HashSet<>();
			splitParametersIntoInputAndOutput(dataset, smallerInputParameters, smallerObservationParameters,
					smallerSelection);
		}

		if (!largerSelection.isEmpty()) {
			largerInputParameters = new HashSet<>();
			largerObservationParameters = new HashSet<>();
			splitParametersIntoInputAndOutput(dataset, largerInputParameters, largerObservationParameters,
					largerSelection);
		}

		DatasetBuilder dsBuilder = new DatasetBuilder(dataset.getRecordType());
		rowLoop: for (DatasetRow row : dataset.getRows()) {
			if (selectionInputParameters != null && !row.getParameters().containsAll(selectionInputParameters)) {
				continue rowLoop;
			}

			if (unEqualInputParameters != null) {
				for (Parameter par : unEqualInputParameters) {
					if (row.getParameters().contains(par)) {
						continue rowLoop;
					}
				}
			}

			if (smallerInputParameters != null) {
				for (Parameter par : smallerInputParameters) {
					Comparable upperLimit = (Comparable) par.getValue();
					Comparable value = (Comparable) row.getValues(par.getName()).get(0);

					if (value.compareTo(upperLimit) > 0) {
						continue rowLoop;
					}
				}
			}

			if (largerInputParameters != null) {
				for (Parameter par : largerInputParameters) {
					Comparable lowerLimit = (Comparable) par.getValue();
					Comparable value = (Comparable) row.getValues(par.getName()).get(0);

					if (value.compareTo(lowerLimit) < 0) {
						continue rowLoop;
					}
				}
			}

			if ((selectionObservationParameters == null || selectionObservationParameters.isEmpty())
					&& (unEqualInputParameters == null || unEqualnObservationParameters.isEmpty())
					&& (smallerObservationParameters == null || smallerObservationParameters.isEmpty())
					&& (largerObservationParameters == null || largerObservationParameters.isEmpty())) {
				dsBuilder.addRowWithoutChecks(row);
			} else {
				dsBuilder.addRowWithoutChecks(applyToWithSubSelection(row, selectionObservationParameters,
						unEqualnObservationParameters, smallerObservationParameters, largerObservationParameters));
			}
		}

		return dsBuilder.build();
	}

	private void splitParametersIntoInputAndOutput(Dataset dataset, Set<Parameter> selectionInputParameters,
			Set<Parameter> selectionObservationParameters, Set<Parameter> selectionSet) {
		Set<String> dsInputParameters = dataset.getInputParameterNames();
		for (Parameter parameter : selectionSet) {
			if (dsInputParameters.contains(parameter.getName())) {
				selectionInputParameters.add(parameter);
			} else {
				selectionObservationParameters.add(parameter);
			}
		}
	}

	/**
	 * applies selection to given data.
	 * 
	 * @param data
	 *            data to select from
	 * @return selected data
	 */
	public DatasetCollection applyTo(DatasetCollection data) {
		DatasetCollectionBuilder dscBuilder = new DatasetCollectionBuilder();
		for (Dataset dataset : data.getDataSets()) {
			Dataset selectedDS = applyTo(dataset);
			if (selectedDS != null) {
				dscBuilder.addDataSet(selectedDS);
			}

		}
		return dscBuilder.build();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((largerSelection == null) ? 0 : largerSelection.hashCode());
		result = prime * result + ((selection == null) ? 0 : selection.hashCode());
		result = prime * result + ((smallerSelection == null) ? 0 : smallerSelection.hashCode());
		result = prime * result + ((unEqualSelection == null) ? 0 : unEqualSelection.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ParameterSelection other = (ParameterSelection) obj;
		if (largerSelection == null) {
			if (other.largerSelection != null) {
				return false;
			}
		} else if (!largerSelection.equals(other.largerSelection)) {
			return false;
		}
		if (selection == null) {
			if (other.selection != null) {
				return false;
			}
		} else if (!selection.equals(other.selection)) {
			return false;
		}
		if (smallerSelection == null) {
			if (other.smallerSelection != null) {
				return false;
			}
		} else if (!smallerSelection.equals(other.smallerSelection)) {
			return false;
		}
		if (unEqualSelection == null) {
			if (other.unEqualSelection != null) {
				return false;
			}
		} else if (!unEqualSelection.equals(other.unEqualSelection)) {
			return false;
		}
		return true;
	}

	

}
