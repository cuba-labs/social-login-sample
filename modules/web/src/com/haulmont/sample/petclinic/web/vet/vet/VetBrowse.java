package com.haulmont.sample.petclinic.web.vet.vet;

import com.haulmont.cuba.gui.Route;
import com.haulmont.cuba.gui.screen.*;
import com.haulmont.sample.petclinic.entity.vet.Vet;

@Route("vets")
@UiController("petclinic_Vet.browse")
@UiDescriptor("vet-browse.xml")
@LookupComponent("vetsTable")
@LoadDataBeforeShow
public class VetBrowse extends StandardLookup<Vet> {
}
