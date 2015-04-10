# coding=utf-8
"""
Django settings for yidingbaoadmin project.

For more information on this file, see
https://docs.djangoproject.com/en/1.6/topics/settings/

For the full list of settings and their values, see
https://docs.djangoproject.com/en/1.6/ref/settings/
"""
SIZE_LEVEL = [u'80-,平米',u'80-150,平米',u'150-300,平米',u'300-500,平米',u'500+,平米']

PRODUCT_SERIAL = [u'度假',u'办公',u'公主',u'休闲',u'甜美']

PRODUCT_TYPE = [u'必订',u'推广',u'普通']

BO_DUAN = [u'冬一波',u'冬二波',u'冬三波']
#BO_DUAN = [u'夏一波',u'夏二波',u'夏三波',u'秋一波',u'秋二波',u'秋三波',u'冬一波',u'冬二波',u'冬三波']
PRODUCT_CLASS = [u'上衣',u'裙',u'裤']
PRICE_ZONE = ['0-500','500-1000','1000-3000','3000-5000']
SIZE = ["S","M","L","XL"]



# Build paths inside the project like this: os.path.join(BASE_DIR, ...)
import os
BASE_DIR = os.path.dirname(os.path.dirname(__file__))

MEDIA_ROOT = BASE_DIR+'\pictures'
MEDIA_URL = '/pictures/'
# Quick-start development settings - unsuitable for production
# See https://docs.djangoproject.com/en/1.6/howto/deployment/checklist/

# SECURITY WARNING: keep the secret key used in production secret!
SECRET_KEY = ')6+_^23-&4_6p!sqdn7mgw@1pqr(=6p$7$pn!-n8(0b_-+5rea'

# SECURITY WARNING: don't run with debug turned on in production!
DEBUG = True

TEMPLATE_DEBUG = True

ALLOWED_HOSTS = []


# Application definition

INSTALLED_APPS = (
    'django.contrib.admin',
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.messages',
    'django.contrib.staticfiles',
    'adminclient',
)

NO_LISTDISPLAY_LINK = ['user order summary', 'all orders']

LOGIN_REDIRECT_URL = '/admin/adminclient/userordersummary?authority=diancang'

MIDDLEWARE_CLASSES = (
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.middleware.common.CommonMiddleware',
    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    'django.middleware.clickjacking.XFrameOptionsMiddleware',
)

ROOT_URLCONF = 'yidingbaoadmin.urls'

WSGI_APPLICATION = 'yidingbaoadmin.wsgi.application'



# Database
# https://docs.djangoproject.com/en/1.6/ref/settings/#databases

DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.mysql',
        'NAME': 'yidingbao',
        'USER': 'root',
        'PASSWORD': 'admin',
        'HOST': '',
        'PORT': '3307',
    }
}

# Internationalization
# https://docs.djangoproject.com/en/1.6/topics/i18n/

LANGUAGE_CODE = 'zh-CN'

TIME_ZONE = 'UTC'

USE_I18N = True

USE_L10N = True

USE_TZ = True


# Static files (CSS, JavaScript, Images)
# https://docs.djangoproject.com/en/1.6/howto/static-files/

STATIC_URL = '/static/'
