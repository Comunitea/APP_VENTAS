/*******************************************************************************
 * MidBan is an Android App which allows to interact with OpenERP
 *     Copyright (C) 2014  CafedeRed
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package com.cafedered.midban.view.activities;

import com.cafedered.midban.R;
import com.cafedered.midban.annotations.Activity;
import com.cafedered.midban.annotations.Background;
import com.cafedered.midban.annotations.Click;
import com.cafedered.midban.conf.ContextAttributes;
import com.cafedered.midban.conf.MidbanApplication;
import com.cafedered.midban.entities.Partner;
import com.cafedered.midban.entities.User;
import com.cafedered.midban.service.repositories.PartnerRepository;
import com.cafedered.midban.utils.LoggerUtil;
import com.cafedered.midban.utils.exceptions.ConfigurationException;
import com.cafedered.midban.utils.exceptions.ServiceException;
import com.cafedered.midban.view.base.BaseSupportActivity;
import com.cafedered.midban.view.fragments.PartnerDetailTabCardFragment;

@Activity(displayAppIcon = true,
        initFragment = PartnerDetailTabCardFragment.class,
        layout = R.layout.activity_partner_edition,
        rootView = R.id.activity_partner_edition_root,
        fragmentContainerView = R.id.activity_partner_edition_container,
        title = R.string.activity_partner_edition_title)
public class PartnerEditionActivity extends BaseSupportActivity {

    @Click(view = R.id.activity_partner_edition_cancel_button)
    public void cancelButtonPressed() {
        try {
            Partner partner = PartnerRepository
                    .getInstance()
                    .getById(
                            ((Partner) MidbanApplication
                                    .getValueFromContext(ContextAttributes.PARTNER_TO_DETAIL))
                                    .getId());
            MidbanApplication.putValueInContext(
                    ContextAttributes.PARTNER_TO_DETAIL, partner);
            finish();
        } catch (ConfigurationException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        } catch (ServiceException e) {
            if (LoggerUtil.isDebugEnabled())
                e.printStackTrace();
        }
    }

    @Background(onOKFinish = true,
            onOK = R.string.activity_partner_edition_saved_and_synchronized,
            onKO = R.string.activity_partner_edition_not_synchronized)
    @Click(view = R.id.activity_partner_edition_save_button)
    public void saveButtonPressed() throws ServiceException {
        User loggedUser = (User) MidbanApplication
                .getValueFromContext(ContextAttributes.LOGGED_USER);
        Partner partnerToUpdate = (Partner) MidbanApplication
                .getValueFromContext(ContextAttributes.PARTNER_TO_DETAIL);
        if (!PartnerRepository.getInstance().updateRemoteObject(
                partnerToUpdate,
                loggedUser.getLogin(), loggedUser.getPasswd()))
            partnerToUpdate.setPendingSynchronization(1);
        PartnerRepository.getInstance().saveOrUpdate(partnerToUpdate);
    }

}
