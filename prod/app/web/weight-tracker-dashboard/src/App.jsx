import React, { useState, useEffect } from 'react';
import { Plus, User, Trash, XIcon } from 'lucide-react';

const API_BASE_URL = 'http://localhost:8080/api'; 

// ... [Mocks remain the same] ...

const profileMock = [
  { id: 1, name: "Gabriel", images: [] },
  { id: 2, name: "Carla", images: [] }
]

const measurementsMock = [
  { id: 1, createdAt: 3421423432, value: 50.2 },
  { id: 2, createdAt: 3421423432, value: 50.2 },
  { id: 3, createdAt: 3421423432, value: 50.2 },
]

function Switch({onChange}) {
  // ... [Switch component remains the same] ...
  const [enabled, setEnabled] = useState(false);

  return (
    <div className='flex gap-2'>
      <p className='text-md text-gray-800'>{enabled ? "kg" : "g"}</p>
    <button
      type="button"
      onClick={() => {onChange(); setEnabled(!enabled)}}
      className={`relative inline-flex h-6 w-11 items-center rounded-full transition 
        ${enabled ? "bg-purple-800" : "bg-gray-300"}`}
    >
      <span
        className={`inline-block h-4 w-4 transform rounded-full bg-white transition
          ${enabled ? "translate-x-6" : "translate-x-1"}`}
      />
    </button>
    </div>
  );
}

export default function App() {
  
  const [profiles, setProfiles] = useState(profileMock);
  const [measurements, setMeasurements] = useState(measurementsMock);
  const [loading, setLoading] = useState(false);
  const [showProfileForm, setShowProfileForm] = useState(false);
  const [showDeleteProfileForm, setShowDeleteProfileForm] = useState(false);
  const [showEditProfileForm, setShowEditProfileForm] = useState(false);
  const [showAddPhotosForm, setShowAddPhotosForm] = useState(false);
  const [showDeleteMeasurementForm, setShowDeleteMeasurementForm] = useState(false);
  const [measurementToDelete, setMeasurementToDelete] = useState(null);
  const [changeUnity, setChangeUnit] = useState(true); 

  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  // Form states
  const [selectedProfile, setSelectedProfile] = useState(null);
  // Changed initial state of images to null/empty array suitable for FileList handling
  const [profileForm, setProfileForm] = useState({ name: '', images: []});
  const [editForm, setEditForm] = useState({});
  const [addImagesForm, setAddImagesForm] = useState();

  function makeRequest(endpoint = '/', request = {}) {
    let url = endpoint;
    let req = Object.assign(
      {
        headers: {
          Accept: 'application/json',
          'Content-Type': 'application/json'
        },
      },
      request
    );
    return fetch(url, req);
  }

  // ... [useEffect hooks remain the same] ...
  useEffect(() => { fetchProfiles(); }, []);

  useEffect(() => {
    if (selectedProfile) {
      fetchMeasurements(selectedProfile);
      const profile = profiles.find(p => p.id === selectedProfile)
      if(profile) {
          setEditForm({name: profile.name})
          setAddImagesForm()
      }
    }
  }, [selectedProfile]);

  const fetchProfiles = async () => {
    // ... [remains the same] ...
    setLoading(true);
    setError(null);
    try {
      const response = await makeRequest(`${API_BASE_URL}/profiles`);
      if (!response.ok) throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      const data = await response.json();
      setProfiles(data);
    } catch (error) {
      console.error('Error fetching profiles:', error);
      setError(`Failed to fetch profiles: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const fetchMeasurements = async (profileId) => {
    // ... [remains the same] ...
    setLoading(true);
    setError(null);
    try {
      const response = await makeRequest(`${API_BASE_URL}/measurements/by-profile/${profileId}`);
      if (!response.ok) throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      const data = await response.json();
      const sorted = data.sort((a, b) => {
        const dateA = a.createdAt ? new Date(a.createdAt) : new Date(a.id);
        const dateB = b.createdAt ? new Date(b.createdAt) : new Date(b.id);
        return dateA - dateB;
      });
      setMeasurements(sorted);
    } catch (error) {
      console.error('Error fetching measurements:', error);
      setError(`Failed to fetch measurements: ${error.message}`);
      setMeasurements([]);
    } finally {
      setLoading(false);
    }
  };

  // Helper function to handle image uploads for both Creation and Adding
  const uploadImages = async (profileId, fileList) => {
    if (!fileList || fileList.length === 0) return;

    const formData = new FormData();
    // Add the profile ID as required by the endpoint
    formData.append('profileId', profileId);

    // Append images
    for (let i = 0; i < fileList.length; i++) {
      formData.append('image', fileList[i]); 
    }

    const response = await makeRequest(`${API_BASE_URL}/embeddings`, {
      method: 'POST',
      headers: {}, // Let browser set boundary
      body: formData
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || `HTTP ${response.status} in image upload`);
    }
    return response;
  };

  const handleCreateProfile = async (e) => {
    // Prevent default form submission if triggered by a form element
    if (e) e.preventDefault();

    if (!profileForm.name) {
      setError('Please fill in the name');
      return;
    }
    setLoading(true);
    setError(null);
    setSuccess(null);
    try {
      // 1. Create the profile first
      const response = await makeRequest(`${API_BASE_URL}/profiles`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: profileForm.name
        })
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || `HTTP ${response.status}`);
      }

      // 2. Get the new ID from the response
      const newProfileData = await response.json();

      // 3. If images were selected, upload them using the new ID
      if (profileForm.images && profileForm.images.length > 0) {
        await uploadImages(newProfileData.id, profileForm.images);
      }

      setProfileForm({ name: '', images: []});
      setShowProfileForm(false);
      setSuccess('Profile created successfully!');
      await fetchProfiles();
    } catch (error) {
      console.error('Error creating profile:', error);
      setError(`Failed to create profile: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateProfile = async () => {
     // ... [remains the same] ...
    if (!selectedProfile || !editForm.name) {
      setError('Please fill all fields');
      return;
    }
    setLoading(true);
    setError(null);
    setSuccess(null);
    try {
      const response = await makeRequest(`${API_BASE_URL}/profiles/${selectedProfile}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: editForm.name
        })
      });
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || `HTTP ${response.status}`);
      }
      setShowEditProfileForm(false);
      setSuccess('Profile updated successfully!');
      await fetchProfiles();
    } catch (error) {
      console.error('Error updating profile:', error);
      setError(`Failed to update profile: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleAddNewImage = async () => {
    if (!addImagesForm || addImagesForm.length === 0) {
      setError('Please select images');
      return;
    }
    setLoading(true);
    setError(null);
    setSuccess(null);

    try {
      // Use the helper function, passing the currently selected profile ID
      await uploadImages(selectedProfile, addImagesForm);

      setShowAddPhotosForm(false);
      setAddImagesForm(null);
      setSuccess('Images saved successfully!');
      await fetchProfiles(); // Refresh to potentially show updated data if needed
    } catch (error) {
      console.error('Error saving images:', error);
      setError(`Failed to save images: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  // ... [deleteMeasurement, deleteProfile, measurementValue, dateFormat remain the same] ...
  const deleteMeasurement = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccess(null);
    try {
      const response = await makeRequest(`${API_BASE_URL}/measurements/${measurementToDelete}`, {
        method: 'DELETE'
      });
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || `HTTP ${response.status}: Failed to delete measurement`);
      }
      setShowDeleteMeasurementForm(false);
      setSuccess('Measurement deleted successfully!');
      await fetchMeasurements(selectedProfile);
    } catch (error) {
      console.error('Error deleting measurement:', error);
      setError(`Failed to delete measurement: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const deleteProfile = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setSuccess(null);
    try {
      const response = await makeRequest(`${API_BASE_URL}/profiles/${selectedProfile}`, {
        method: 'DELETE'
      });
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || `HTTP ${response.status}: Failed to delete profile`);
      }
      setShowDeleteProfileForm(false);
      setSelectedProfile(null);
      setMeasurements([]);
      setSuccess('Profile deleted successfully!');
      await fetchProfiles();
    } catch (error) {
      console.error('Error deleting profile:', error);
      setError(`Failed to delete profile: ${error.message}`);
    } finally {
      setLoading(false);
    }
  };

  const measurementValue = (value) => {
    if(changeUnity) return value*1000;
    else return value
  }

  const dateFormat = (measurementDate) => {
      const date = new Date(measurementDate);
      return (
        date.getDate().toString().padStart(2, "0") + "/" +
        (date.getMonth() + 1).toString().padStart(2, "0") + "/" +
        date.getFullYear() + " - " +
        date.getHours().toString().padStart(2, "0") + ":" +
        date.getMinutes().toString().padStart(2, "0") + " " 
      )
  }

  return (
    <div className="min-h-screen bg-gray-50 relative">
      <div className="max-w-7xl mx-auto p-6">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-light text-gray-900 mb-2">Smart Scale PoC Dashboard</h1>
        </div>

        {/* Error/Success Messages */}
        {error && (
          <div className="mb-6 bg-red-50 border border-red-200 text-red-800 px-4 py-3 rounded-lg flex items-start justify-between">
            <span className="text-sm">{error}</span>
            <button onClick={() => setError(null)} className="text-red-600 hover:text-red-800 font-bold">×</button>
          </div>
        )}
        {success && (
          <div className="mb-6 bg-green-50 border border-green-200 text-green-800 px-4 py-3 rounded-lg flex items-start justify-between">
            <span className="text-sm">{success}</span>
            <button onClick={() => setSuccess(null)} className="text-green-600 hover:text-green-800 font-bold">×</button>
          </div>
        )}

        {/* Profile Selector */}
        <div className="bg-white rounded-lg shadow-sm p-4 mb-6">
          <div className="flex items-center justify-between mb-2">
            <div className="flex items-center gap-4 flex-1">
              <User className="text-gray-400" size={24} />
              <select
                autoComplete="off"
                value={selectedProfile || ""}
                onChange={(e) => setSelectedProfile(e.target.value ? e.target.value : null)}
                disabled={profiles.length === 0}
                className="flex-1 max-w-xs border-gray-300 h-12 p-4 bg-white border rounded-md text-sm focus:ring-purple-300 focus:border-purple-400"
              >
                {profiles.length === 0 && <option value="">No profiles</option>}
                <option value="">Select...</option>

                {profiles.map(profile => (
                  <option key={profile.id} value={profile.id}>
                    {profile.name}
                  </option>
                ))}
              </select>
            </div>
            <div className="flex gap-2">
              {selectedProfile && (
                <button
                  onClick={() => setShowDeleteProfileForm(true)}
                  className="px-4 py-2 text-red-600 border border-red-300 rounded-md hover:bg-red-50 text-sm"
                >
                  Delete Profile
                </button>
              )}
              {selectedProfile && (
                <button
                  onClick={() => setShowEditProfileForm(true)}
                  className="px-4 py-2 text-green-600 border border-green-300 rounded-md hover:bg-green-50 text-sm"
                >
                  Edit Profile
                </button>
              )}
              {selectedProfile && (
                <button
                  onClick={() => setShowAddPhotosForm(true)}
                  className="px-4 py-2 text-purple-600 border border-purple-300 rounded-md hover:bg-purple-50 text-sm"
                >
                  Add images
                </button>
              )}
              
              <button
                onClick={() => setShowProfileForm(!showProfileForm)}
                className="flex items-center gap-2 px-4 py-3 bg-gray-900 text-white rounded-md hover:bg-gray-700 text-sm"
              >
                <Plus size={16} />
                New Profile
              </button>
            </div>
          </div>

          {/* MODAL NEW PROFILE */}
          {showProfileForm && (
            <form className="absolute w-[600px] top-1/4 border border-gray-100 left-1/3 p-6 rounded-lg bg-white shadow-sm flex flex-col gap-4 items-end">
              <XIcon size={20} className='cursor-pointer' onClick={() => {
                setShowProfileForm(false);
                setProfileForm({ name: '', images: []});
              }}/>
              <div className='w-full flex flex-col gap-2'>
                <label className='text-gray-900'>Full name</label>
                <input
                  type="text"
                  placeholder="Insert the full name"
                  required
                  value={profileForm.name}
                  onChange={(e) => setProfileForm({ ...profileForm, name: e.target.value })}
                  className="w-full appearance-none outline-none ring-none border border-gray-300 px-4 py-3 rounded-md focus:border-purple-400"
                />
              </div>
              <div className='w-full flex flex-col gap-2'>
                <label className='text-gray-900'>Add images</label>
                <label className="flex flex-col items-center justify-center w-full h-40 border-2 border-dashed border-gray-300 rounded-lg cursor-pointer hover:bg-gray-50 transition">
                  {/* Fixed conditional logic to check if images exist properly */}
                  <div className={`${!profileForm.images || profileForm.images.length === 0 ? "text-md text-gray-600" : "text-md text-green-600"}`}>
                    {!profileForm.images || profileForm.images.length === 0 ? "Select images" : `Image selected!`}
                  </div>
                  <input
                    type="file"
                    accept="image/*"
                    // IMPORTANT: Don't set value={profileForm.images} for file inputs
                    // Fixed: Use e.target.files instead of e.target.value
                    onChange={(e) => setProfileForm({ ...profileForm, images: e.target.files })}
                    multiple
                    required
                    className="hidden"
                  />
                </label>
              </div>
              <div className="flex gap-2">
                <button
                  type="button" // Changed to type="button" to prevent default submit loop, handled in onClick
                  onClick={handleCreateProfile}
                  disabled={loading}
                  className="px-4 py-3 bg-gray-900 text-white rounded-md hover:bg-gray-800 text-sm disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {loading ? 'Creating...' : 'Create Profile'}
                </button>
                <button
                  type='reset'
                  onClick={() => {
                    setShowProfileForm(false);
                    setProfileForm({ name: '', images: []});
                  }}
                  className="px-4 py-3 border border-gray-300 rounded-md hover:bg-gray-50 text-sm"
                >
                  Cancel
                </button>
              </div>
            </form>
          )}
        </div>

        {/* ... [MODAL EDIT PROFILE, MODAL DELETE PROFILE remain the same] ... */}
        {showEditProfileForm && (
            <form className="absolute w-[600px] top-1/4 border border-gray-100 left-1/3 p-6 rounded-lg bg-white shadow-sm flex flex-col gap-4 items-end">
              <XIcon size={20} className='cursor-pointer' onClick={() => {setShowEditProfileForm(false)}}/>
              <div className='w-full flex flex-col gap-2'>
                <label className='text-gray-900'>Full name</label>
                <input
                  type="text"
                  placeholder="Insert the full name"
                  required
                  value={editForm.name}
                  onChange={(e) => setEditForm({ ...editForm, name: e.target.value })}
                  className="w-full appearance-none outline-none ring-none border border-gray-300 px-4 py-3 rounded-md focus:border-purple-400"
                />
              </div>
              <div className="flex gap-2">
                <button
                  type="submit"
                  onClick={handleUpdateProfile}
                  disabled={loading}
                  className="px-4 py-3 bg-gray-900 text-white rounded-md hover:bg-gray-800 text-sm disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {loading ? 'Creating...' : 'Save Changes'}
                </button>
                <button
                  type='reset'
                  onClick={() => setShowEditProfileForm(false)}
                  className="px-4 py-3 border border-gray-300 rounded-md hover:bg-gray-50 text-sm"
                >
                  Cancel
                </button>
              </div>
            </form>
          )}

        {showDeleteProfileForm && (
            <form className="absolute w-[600px] top-1/4 border border-gray-100 left-1/3 p-6 rounded-lg bg-white shadow-sm flex flex-col gap-4 ">
              <div className='w-full flex justify-end'>
                <XIcon size={20} className='cursor-pointer' onClick={() => {setShowDeleteProfileForm(false)}}/>
              </div>
              <h1 className='text-xl'>Are you sure you want to delete this profile?</h1>
              <div className="gap-2 grid grid-cols-2">
                <button
                  type="button"
                  onClick={(e) => {
                    e.preventDefault();
                    deleteProfile(e);
                  }}
                  disabled={loading}
                  className="px-4 py-3 w-full bg-red-700 text-white rounded-md hover:bg-red-800 text-sm disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {loading ? 'Deleting...' : 'Delete'}
                </button>
                <button
                  type='reset'
                  onClick={() => setShowDeleteProfileForm(false)}
                  className="px-4 py-3 w-full border border-gray-300 rounded-md hover:bg-gray-50 text-sm"
                >
                  Cancel
                </button>
              </div>
            </form>
          )}

        {/* MODAL ADD PHOTOS */}
        {showAddPhotosForm && (
            <form className="absolute w-[600px] top-1/4 border border-gray-100 left-1/3 p-6 rounded-lg bg-white shadow-sm flex flex-col gap-4 ">
              <div className='w-full flex justify-end'>
                <XIcon size={20} className='cursor-pointer' onClick={() => {setShowAddPhotosForm(false)}}/>
              </div>
              <div className='w-full flex flex-col gap-2'>
                <label className='text-gray-900'>Add images</label>
                <label className="flex flex-col items-center justify-center w-full h-40 border-2 border-dashed border-gray-300 rounded-lg cursor-pointer hover:bg-gray-50 transition">
                  <div className={`${!addImagesForm ? "text-md text-gray-600" : "text-md text-green-600"}`}>{!addImagesForm ? "Select images" : `${addImagesForm.length} image(s) selected`}</div>
                  <input
                    type="file"
                    accept="image/*"
                    onChange={(e) => setAddImagesForm(e.target.files)}
                    multiple
                    required
                    className="hidden"
                  />
                </label>
              </div>
              <div className="gap-2 grid grid-cols-2">
                <button
                  type="button" // Changed to type="button"
                  onClick={handleAddNewImage}
                  disabled={loading}
                  className="px-4 py-3 bg-gray-900 text-white rounded-md hover:bg-gray-800 text-sm disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {loading ? 'Creating...' : 'Add Photos'}
                </button>
                <button
                  type='reset'
                  onClick={() => setShowAddPhotosForm(false)}
                  className="px-4 py-3 w-full border border-gray-300 rounded-md hover:bg-gray-50 text-sm"
                >
                  Cancel
                </button>
              </div>
            </form>
          )}

        {/* MODAL DELETE MEASUREMENT */}
        {showDeleteMeasurementForm && (
            <form className="absolute w-[600px] top-1/4 border border-gray-100 left-1/3 p-6 rounded-lg bg-white shadow-sm flex flex-col gap-4 ">
              <div className='w-full flex justify-end'>
                <XIcon size={20} className='cursor-pointer' onClick={() => {setShowDeleteMeasurementForm(false)}}/>
              </div>
              <h1 className='text-xl'>Are you sure you want to delete this measurement?</h1>
              <div className="gap-2 grid grid-cols-2">
                <button
                  type="button"
                  onClick={(e) => {
                    const selectedMeasurement = measurements.find(m => m.id === measurementToDelete);
                    if (selectedMeasurement) {
                      deleteMeasurement(e);
                      setShowDeleteMeasurementForm(false);
                    }
                  }}
                  disabled={loading}
                  className="px-4 py-3 w-full bg-red-700 text-white rounded-md hover:bg-red-800 text-sm disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {loading ? 'Deleting...' : 'Delete'}
                </button>
                <button
                  type='button'
                  onClick={() => setShowDeleteMeasurementForm(false)}
                  className="px-4 py-3 w-full border border-gray-300 rounded-md hover:bg-gray-50 text-sm"
                >
                  Cancel
                </button>
              </div>
            </form>
          )}

        {/* TABLE */}
        {selectedProfile && (
          <>
            {/* Measurements List */}
            <div className="bg-white rounded-lg shadow-sm p-6">
              <div className="flex items-center justify-between mb-4">
                <h2 className="text-xl text-gray-900">Measurements</h2>
                {selectedProfile && <Switch onChange={() => setChangeUnit(!changeUnity)} />}
              </div>


              {loading ? (
                <div className="text-center py-8 text-gray-500">Loading...</div>
              ) : measurements.length === 0 ? (
                <div className="text-center py-8 text-gray-500">No measurements yet.</div>
              ) : (
                <div className="space-y-2">
                  <div className='grid grid-cols-3 gap-2 w-full'>
                    <p className="text-lg text-gray-800 pl-4">Date</p>
                    <p className="text-lg text-gray-800 text-center">Measurement</p>
                    <p className="text-lg text-gray-800 text-right pr-20">Delete</p>
                  </div>
                  {measurements.slice().reverse().map(measurement => (
                    <div
                      key={measurement.id}
                      className="flex items-center justify-between py-3 px-4 border border-gray-300 hover:bg-gray-50 rounded-md"
                    >
                      <div className="grid grid-cols-3 gap-2 w-full">
                        <p className="text-lg text-gray-600 ">
                          {dateFormat(measurement.recordedAt)}
                        </p>
                        <p className="text-lg text-gray-600 text-center">
                          {measurementValue(measurement.value)} {!changeUnity ? "kg" : "g"}
                        </p>
                        <div className='w-full flex justify-end pr-20'>
                          <button
                            onClick={() => {
                              setMeasurementToDelete(measurement.id);
                              setShowDeleteMeasurementForm(true);
                            }}
                            className="text-red-500 hover:text-red-700 text-sm"
                          >
                            <Trash size={20} />
                          </button>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </>
        )}

        {!selectedProfile && !loading && profiles.length === 0 && (
          <div className="bg-white rounded-lg shadow-sm p-12 text-center">
            <User className="mx-auto text-gray-300 mb-4" size={48} />
            <p className="text-gray-500 mb-4">No profiles yet. Create your first profile to get started!</p>
            <button
              onClick={() => setShowProfileForm(true)}
              className="px-6 py-3 bg-gray-900 text-white rounded-md hover:bg-gray-800"
            >
              Create Profile
            </button>
          </div>
        )}
      </div>
    </div>
  );
}