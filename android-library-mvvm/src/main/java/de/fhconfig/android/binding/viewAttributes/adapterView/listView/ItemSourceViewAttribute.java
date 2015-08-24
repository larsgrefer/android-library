package de.fhconfig.android.binding.viewAttributes.adapterView.listView;

import android.widget.Adapter;
import android.widget.Filter;
import android.widget.ListView;

import java.util.Collection;

import de.fhconfig.android.binding.Binder;
import de.fhconfig.android.binding.BindingType;
import de.fhconfig.android.binding.IObservable;
import de.fhconfig.android.binding.Observer;
import de.fhconfig.android.binding.ViewAttribute;
import de.fhconfig.android.binding.exception.AttributeNotDefinedException;
import de.fhconfig.android.binding.viewAttributes.templates.Layout;

/**
 * Item Source of ListView
 * Supplying the IObservableCollection to this and Item Template together, adapter will be created automatically
 * Due to the complexity of this API, we recommend to switch to binding:adapter instead of this
 * e.g.  binding:adapter="ADAPTER({source=..., template=...})
 * which will give more fine-grain control on adapter generated
 *
 * @author andy
 * @name itemSource
 * @widget ListView
 * @type Object
 * @accepts Object
 * @category list
 * @related
 */
public class ItemSourceViewAttribute extends ViewAttribute<ListView, Object> {

	Layout template, spinnerTemplate;
	Filter filter;
	ViewAttribute<?, Layout> itemTemplateAttr, spinnerTemplateAttr;
	ViewAttribute<?, Filter> filterAttr;
	Object mValue;

	private Observer templateObserver = new Observer() {
		public void onPropertyChanged(IObservable<?> prop,
		                              Collection<Object> initiators) {
			template = itemTemplateAttr.get();
			spinnerTemplate = spinnerTemplateAttr.get();
			doSetAttributeValue(mValue);
		}
	};

	@SuppressWarnings("unchecked")
	public ItemSourceViewAttribute(ListView view, String attributeName) {
		super(Object.class, view, attributeName);

		try {
			itemTemplateAttr = (ViewAttribute<?, Layout>) Binder.getAttributeForView(getView(), "itemTemplate");
			itemTemplateAttr.subscribe(templateObserver);
			spinnerTemplateAttr = (ViewAttribute<?, Layout>) Binder.getAttributeForView(getView(), "spinnerTemplate");
			spinnerTemplateAttr.subscribe(templateObserver);
			template = itemTemplateAttr.get();
			spinnerTemplate = spinnerTemplateAttr.get();
			filterAttr = (ViewAttribute<?, Filter>) Binder.getAttributeForView(getView(), "filter");
			filter = filterAttr.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void doSetAttributeValue(Object newValue) {
		if (getView() == null) return;
		mValue = newValue;

		if (newValue == null)
			return;

		if (newValue instanceof Adapter) {
			try {
				((ViewAttribute<?, Adapter>) Binder.getAttributeForView(getView(), "adapter")).set((Adapter) newValue);
			} catch (AttributeNotDefinedException e) {
				e.printStackTrace();
			}
			return;
		}

		if (template == null) return;

		spinnerTemplate = spinnerTemplate == null ? template : spinnerTemplate;

		try {
			Adapter adapter = de.fhconfig.android.binding.collections.Utility.getSimpleAdapter
					(getView().getContext(), newValue, spinnerTemplate, template, filter);
			((ViewAttribute<?, Adapter>) Binder.getAttributeForView(getView(), "adapter")).set(adapter);
			ViewAttribute<?, Integer> SelectedPosition = (ViewAttribute<?, Integer>) Binder.getAttributeForView(getView(), "selectedPosition");
			getView().setSelection(SelectedPosition.get());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object get() {
		return mValue;
	}

	@Override
	protected BindingType AcceptThisTypeAs(Class<?> type) {
		return BindingType.OneWay;
	}
}