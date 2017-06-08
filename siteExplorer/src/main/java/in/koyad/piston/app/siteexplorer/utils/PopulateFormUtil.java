/*
 * Copyright (c) 2012-2017 Shailendra Singh <shailendra_01@outlook.com>
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
package in.koyad.piston.app.siteexplorer.utils;

import java.util.ArrayList;
import java.util.List;

import org.koyad.piston.business.model.Page;
import org.koyad.piston.business.model.SecurityAcl;
import org.koyad.piston.business.model.Site;
import org.koyad.piston.business.model.enums.RoleType;

import in.koyad.piston.app.siteexplorer.forms.PageDetailsPluginForm;
import in.koyad.piston.app.siteexplorer.forms.ResourcePluginForm;
import in.koyad.piston.app.siteexplorer.forms.SiteDetailsPluginForm;
import in.koyad.piston.common.util.BeanPropertyUtils;
import in.koyad.piston.common.util.LogUtil;

public class PopulateFormUtil {

	private static final LogUtil LOGGER = LogUtil.getLogger(PopulateFormUtil.class);
	
	public static void populateSiteDetails(SiteDetailsPluginForm form, Site site) {
		//copy id, name etc.
		BeanPropertyUtils.copyProperties(form, site);
		
		//copy frame name
		form.setFrameId(site.getFrameId());
		
		//copy title, mapping etc.
		BeanPropertyUtils.copyProperties(form, site.getMetadata());
		
		//copy permissions
		copyAcls(site.getAcls(), form);
	}
	
	public static void populatePageDetails(PageDetailsPluginForm form, Page page) {
		//copy id, name etc.
		BeanPropertyUtils.copyProperties(form, page);
		form.setSiteId(page.getSite().getId());
		
		//copy title, mapping etc.
		BeanPropertyUtils.copyProperties(form, page.getMetadata());
		
		//copy permissions
		copyAcls(page.getAcls(), form);
	}
	
	private static void copyAcls(List<SecurityAcl> acls, ResourcePluginForm form) {
		for(SecurityAcl acl : acls) {
			List<String> principals = new ArrayList<>();
			acl.getMembers().getUsers().forEach(user -> principals.add("user:".concat(user)));
			acl.getMembers().getGroups().forEach(group -> principals.add("group:".concat(group)));
			
//			for(Principal principal :  acl.getMembers()) {
//				String prefix  = "";
//				if(principal instanceof User) {
//					prefix = "user";
//				} else if(principal instanceof Group) {
//					prefix = "group";
//				}
//				principals.add(prefix + ":" + principal.getExternalId()); 
//			}
			RoleType roleType = acl.getRole();
			switch(roleType) {
				case MANAGER:
					form.setManager(principals.toArray(new String[principals.size()]));
					break;
				case EDITOR:
					form.setEditor(principals.toArray(new String[principals.size()]));
					break;
				case USER:
					form.setUser(principals.toArray(new String[principals.size()]));
					break;
				default:
					LOGGER.debug("No match found.");
			}
		}
	}
}
