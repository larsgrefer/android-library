package de.fhconfig.android.library.reflection;

import com.google.common.base.Optional;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import de.fhconfig.android.library.injection.annotation.AttributeType;
import de.fhconfig.android.library.injection.annotation.InjectAttribute;
import de.fhconfig.android.library.injection.annotation.InjectResource;
import de.fhconfig.android.library.injection.annotation.InjectView;
import de.fhconfig.android.library.injection.annotation.ResourceType;
import de.fhconfig.android.library.util.predicate.Predicate;

import static org.junit.Assert.*;

public class ReflectionTest {

	@Test
	public void testGetDeclaredFields() throws Exception {
		List<Field> declaredFields = Reflection.getAllFields(ClassC.class, Optional.<Class<? super ClassC>>of(Object.class), new Predicate<Field>() {
			@Override
			public boolean apply(Field input) {
				return input.isAnnotationPresent(InjectView.class);
			}
		});

		assertEquals(2, declaredFields.size());

		declaredFields = Reflection.getAllFields(ClassC.class, new FieldAnnotationPredicate(InjectAttribute.class));
		assertEquals(1, declaredFields.size());

		declaredFields = Reflection.getAllFields(ClassC.class, new FieldAnnotationPredicate(InjectResource.class));
		assertEquals(1, declaredFields.size());

		declaredFields = Reflection.getAllFields(ClassC.class, ClassB.class, new FieldAnnotationPredicate(InjectView.class));
		assertEquals(1, declaredFields.size());
	}

	class ClassA{
		@InjectView()
		private int aa;
		@InjectAttribute(id = 55, type = AttributeType.FLOAT)
		private double ab;
	}

	class ClassB extends ClassA{
		public float ba;
		@InjectResource(id = 44, type = ResourceType.MOVIE)
		public Object ab;
	}

	class ClassC extends ClassB{
		ClassA ca;
		@InjectView()
		ClassB cb;
	}
}